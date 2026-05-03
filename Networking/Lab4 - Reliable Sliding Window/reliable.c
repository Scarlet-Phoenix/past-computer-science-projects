#include <assert.h>
#include <errno.h>
#include <inttypes.h>
#include <poll.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <time.h>
#include <sys/time.h>
#include <sys/socket.h>
#include <sys/uio.h>
#include <netinet/in.h>

#include "rlib.h"

#define DATA_HDRLEN 12
#define ACK_HDRLEN   8
#define CURRENT_WINDOW r->winbuf[i]

/*
 * sender/receiver window buffer information
 */
struct window_buf_s {
  uint32_t        len;       // packet length
  uint32_t        valid;     // this buffer contains a received packet (recv-only)
  uint32_t        ack;       // this buffer has been acknowledeged by receiver (send-only)
  uint64_t        timestamp; // when this packet was sent (send-only)
  packet_t        p;         // buffered packet
};
typedef struct window_buf_s window_buf_t;


/*
 * reliable connection state information
 */
struct reliable_state {
  rdt_t *next;			         // this is a linked list of active connections
  rdt_t **prev;
  conn_t *c;			           // rlib connection object
  window_buf_t **winbuf; //list of all the window buffers we have, 
  int bufindex; 
  uint32_t myackno; 
  int senderackno; 
  int order_sequ; 
  int do_timer; 
  //int ack_recieved; 
  int eof_recieved; 
  int currseqno; 
  int datsize; 
  int winsize; 
  int timejump; 
  int expected_next;
  int retransmits_attempted; 

  // you will need to add more fields to this structure
};


/*
 * global variables
 */
rdt_t *rdt_list;

int random_bool() //meant to simulate the network dropping packets. 
{
  return rand() % 2;
}

void print_window(window_buf_t *win, char* controlstr){
  fprintf(stderr, "%s ack: %d, valid: %d, len: %d\n", controlstr, win->ack, win->valid, win->len);
}
window_buf_t* make_window_buf(rdt_t *r){
  //0 for data, 1 for ack. 
  window_buf_t *ret = malloc(sizeof(window_buf_t));
  if (ret == NULL){
  //  fprintf(stderr, "make_window_buf: malloc returned null.\n"); 
    return NULL; 
  } 
  // ret->p = *pak;
  //ret->ack = ntohl(pak->ackno); 
  //ret->len = ntohs(pak->len); 
  return ret; 
}
//mode 0: sender
//mode 1: reciever
int are_buffers_clear(rdt_t *r, int mode){
  for (int i = 0; i < sizeof(r->winbuf); i++){
    if(mode && CURRENT_WINDOW->valid == 1){
      return 0;
    }
    if (!mode && CURRENT_WINDOW->ack > 1){
      return 0;
    }
  }
  return 1; 
}

//sorts data packet window in ter:w
//ms of seqno
//inefficent. 
/*void window_sort(rdt_t *r)
{
  if (sizeof(r->winbuf) == 1) return; 
  int i, j;
  int swapped;
  for (i = 0; i < sizeof(r->winbuf) - 1; i++)
  {
    swapped = 0; 
    for (j = 0; j < sizeof(r->winbuf) - i - 1; j++)
    {
      if (ntohl(r->winbuf[j]->p.seqno) > ntohl(r->winbuf[j + 1]->p.seqno)){
        void *temp = r->winbuf[j];
        r->winbuf[j] = r->winbuf[j + 1]; 
        r->winbuf[j + 1] = temp;
        swapped = 1;
      }
    }
    if (!swapped) 
      break;
  }
}
*/
/*int is_window_gap(rdt_t *r){unused. 
  if (sizeof(r->winbuf) == 1) return 1; 
  int i;
  for (i = 0; i < sizeof(r->winbuf) - 1; i++){
    if (ntohl(r->winbuf[i]->p.seqno) + 1 != ntohl(r->winbuf[i + 1]->p.seqno)){
      return 1;
    }
  }
  return 0;
}
*/
packet_t* make_ack_pack(rdt_t *r){
  packet_t *pak = malloc(sizeof(packet_t)); 
  pak->len = ntohs(ACK_HDRLEN); 
  pak->ackno = ntohl(r->myackno); 
  pak->cksum = 0; 
  pak->cksum = cksum(pak, ACK_HDRLEN); 
  return pak; 
} 

//mode 0 = sender;
//mode 1 = reciever;
void window_push(rdt_t *r, packet_t *pak, int mode){
  for (int i = 0; i < sizeof(r->winbuf); i++){
    if (!(r->winbuf[i]->valid) && mode == 1){
     // fprintf(stderr, "window_push: mode reciever.\n"); 
      r->winbuf[i]->p = *pak; 
      r->winbuf[i]->valid = 1; 
      return; 
    }else{
      // if it's the sender, we find a window that doesn't have a packet yet, and set it
      // directly checking if there's a packet stored there throws a typeerror, but we can
      // just set the length to 0 when the window is unused for now. 
      if (r->winbuf[i]->ack == 1 && mode == 0){ // if this is the sender, and the position is unused.
    //    fprintf(stderr, "window_push: mode reciever."); 
        r->winbuf[i]->p = *pak; 
        r->winbuf[i]->len = ntohs(pak->len);
        r->winbuf[i]->ack = 0; 
        CURRENT_WINDOW->timestamp = time(NULL); 
        return;
      }
    }
  }
   // fprintf(stderr, "Window push: was not able to find room for packet on window.\n"); 
}
  
/**
 * rdt_create - creates a new reliable protocol session.
 * @param c  - connection object (when running in single-connection mode, NULL otherwise)
 * @param ss - sockaddr info (when running in multi-connection mode, NULL otherwise)
 * @param cc - global configuration information
 * @returns new reliable state structure, NULL on failure
 */
rdt_t *rdt_create(conn_t *c, const struct sockaddr_storage *ss, const struct config_common *cc) {
  rdt_t *r;

  r = xmalloc (sizeof (*r));
  memset (r, 0, sizeof (*r));

  if (!c) {
    c = conn_create (r, ss);
    if (!c) {
      free (r);
      return NULL;
    }
  }

  r->c = c;
  r->next = rdt_list;
  r->prev = &rdt_list;
  if (rdt_list)
    rdt_list->prev = &r->next;
  rdt_list = r;

  r->winbuf = (window_buf_t **) calloc(cc->window, sizeof(window_buf_t *));
  fprintf(stderr, "make conn: window size is %u\n", cc->window);
  if (r->winbuf == NULL) {free(r->winbuf); free(r);  exit(1);}  
  for (int i = 0; i < sizeof(r->winbuf); i++)
  {
    r->winbuf[i] = make_window_buf(r);
    r->winbuf[i]->valid = 0; 
    r->winbuf[i]->len = 0; 
    r->winbuf[i]->ack = 1;
  }
  r->expected_next = 1; 
  r->myackno = 1; 
  r->currseqno = 1; 
  r->order_sequ = 0; 
  r->senderackno = 0;
  r->datsize = 500; 
  // add additional initialization code here
  r->bufindex = 0; 
  r->winsize = cc->window; 
  r->do_timer = 0; 
  r->timejump = cc->timer;
  r->retransmits_attempted = 0; 
  return r;
}



/**
 * rdt_destroy - shutdown a reliable protocol session
 * @param r - reliable connection to close
 */
void rdt_destroy(rdt_t *r) {
  if (r->next)
    r->next->prev = r->prev;
  *r->prev = r->next;
  conn_destroy (r->c);
  free(r->winbuf); 
 
 // fprintf(stderr, "Destroyed rdt\n");

  // free any other allocated memory here
}

//ALL PACKETS CANNOT BE DELIVERED TO APP LAYER UNTIL 

/**
 * rdt_recvpkt - receive a packet from the unreliable network layer
 * @param r - reliable connection state information
 * @param pkt - received packet
 * @param n - size of received packet
 */
void rdt_recvpkt(rdt_t *r, packet_t *pkt, size_t n) {
  // implement this function 

 // print_pkt(pkt, "recv'd paket: ", n);
 // if (random_bool()){
   /// fprintf(stderr, "rdt_recvpkt: simulating packet drop. returning from function and doing nothing else\n");
   /// meant to simulate packet dropping while using localhost
   // return; //THIS MAY CAUSE SEGFAULTS
  //} 
  uint16_t packsum = pkt->cksum; 
  uint16_t pktlen = ntohs(pkt->len); 
  pkt->cksum = 0; 
  if (cksum(pkt, pktlen) != packsum){
   // fprintf(stderr, "rdt_recvpkt: corrupted packet received. not sending back an ack.\n"); 
    return; 
  } 
  uint32_t packet_ack_no = ntohl(pkt->ackno);
  int found_acked_packet = -1; 
  if (pktlen == ACK_HDRLEN){
   // fprintf(stderr, "rdt_recvpkt: recieved ack num: %u\n", packet_ack_no); 
    found_acked_packet = 0;
    r->retransmits_attempted = 0; 
    r->myackno = packet_ack_no;
    for (int i = 0; i < r->winsize; i++) 
    { // this segment of code never activates. idk why. 
      if (r->winbuf[i] != 0){
        if (ntohl(r->winbuf[i]->p.seqno) == packet_ack_no){
     //     fprintf(stderr, "rdt_rectpkt: setting packet with ackno.%u to yes\n", packet_ack_no); 
          r->winbuf[i]->ack = 1; 
       //   fprintf(stderr, "set packet in pos %u to acked.\n", i); 
          found_acked_packet = 1; //needed to decide whether or not to 
                                  //attempt to slide the window. 
        }
      }
    }
    //MAKE ACK PACKET 
  }else{
    //DATA PACKET RECIEVED!
    if (ntohl(pkt->seqno) < r->myackno){
      //if they're sending packets we've already delivered, send an ack for that number, and do nothing else.  
      packet_t *temmak = make_ack_pack(r);
      conn_sendpkt(r->c, temmak, ACK_HDRLEN); 
      free(temmak);
     // fprintf(stderr, "rdt_recvpkt: resent ack to duplicate packet.\n"); 
      return;
    }  
    if (pktlen == DATA_HDRLEN){
      r->eof_recieved = 1; 
      //fprintf(stderr, "rdt_recvpkt: recieved EOF. \n");
      window_push(r, pkt, 1); 
    }else{ 
     // print_pkt(pkt, "rdt_recvpkt: recieved packet ", pktlen); 
      window_push(r, pkt, 1);
    }
  }
  if (found_acked_packet == -1) {
    rdt_output(r); //this was a recieve event. goto deliver. 
  /*}else{
    if (found_acked_packet == 1) {
      //sender: we found the packet that this ack was meant for.
      r->currseqno = packet_ack_no; //reciever has acked all packets with a 
                                    //seqno earlier than this. 
                                    //the window can now be flagged for cleaning. 
    }else{
     // fprintf(stderr, "rdt_recvpkt: a packet recieved was an ack packet, however the packet the ack was for was not found.\n"); 
    }
    */
  }
  //fprintf(stderr, "%u\n", found_acked_packet); 
  //rdt_output(r); 
}



/**
 * rdt_read - read packet from application and send to network layer
 * @param r - reliable connection state information
 */
void rdt_read(rdt_t *r) {
  fprintf(stderr, "rdt_read: called\n");
  // implement this functioni
  packet_t *currpak = malloc(sizeof(packet_t));
 // window_buf_t *win = malloc(sizeof(window_buf_t)); 
  int connret = conn_input(r->c, currpak->data,  r->datsize);
  if (connret == -1)
  {
    currpak->ackno = htonl(r->myackno);
    currpak->len = htons(DATA_HDRLEN); 
    currpak->seqno = htonl(r->currseqno); 
    currpak->cksum = 0; 
    currpak->cksum = cksum(currpak, DATA_HDRLEN); 
   // print_pkt(currpak, "sender: EOF packet", DATA_HDRLEN);
    conn_sendpkt(r->c, currpak, DATA_HDRLEN); 
    //r->do_timer = 1; 
    /*win->p = *currpak; 
    win->len = DATA_HDRLEN; 
    win->ack = 0; //HAS NOT BEEN ACKNOWLEDGED
    win->timestamp = time(NULL);
    win->valid = 2; */
    //window_buf_t *win = make_window_buf(r, currpak);  
   // conn_sendpkt(r->c, currpak, DATA_HDRLEN);
    //int placecheck = 0;
     window_push(r, currpak, 0);
    r->do_timer = 1; 
  }else{
    currpak->ackno = htonl(r->myackno); 
    currpak->len = htons(DATA_HDRLEN + strlen(currpak->data)); 
    currpak->seqno = htonl(r->currseqno); 
    currpak->cksum = 0; 
    currpak->cksum = cksum(currpak, DATA_HDRLEN + strlen(currpak->data));
   // print_pkt(currpak, "sender: current packet:", DATA_HDRLEN + strlen(currpak->data));
    conn_sendpkt(r->c, currpak, strlen(currpak->data) + DATA_HDRLEN);
    r->currseqno++;
    /*win->p = *currpak; 
    win->len = DATA_HDRLEN + strlen(appli_buf); 
    win->ack = 0;
    win->timestamp = time(NULL); 
    win->valid = 2;*/
    //window_buf_t *win = make_window_buf(r, currpak); 
    //win->timestamp = time(NULL); 
   // conn_sendpkt(r->c, currpak, sizeof(*currpak));
   // int placecheck = 0; 
     window_push(r, currpak, 0); 
    //fprintf(stderr, "Placecheck %u\n", placecheck); 
    r->do_timer = 1;  
  }
}

/**
 * rdt_output - callback for delivering packet to application layer if buffer was full
 * @param r - reliable connection state information
 */
void rdt_output(rdt_t *r) {
  // implement this function
  //fprintf(stderr, "current ack no %i\n", r->myackno); 
  for(int i = 0; i < sizeof(r->winbuf); i++)
  {
    if (r->winbuf[i]->valid){
    //  fprintf(stderr, "rdt_output: curr pak is valid.\n"); 
      if (ntohs(CURRENT_WINDOW->p.len) == DATA_HDRLEN) 
      {
        //EOF
        rdt_destroy(r);
        return;
      }
      if (ntohl(r->winbuf[i]->p.seqno) == r->myackno){
        while (1){
           //let's not get complacent here. 
           if (ntohs(CURRENT_WINDOW->p.len) < (conn_bufspace(r->c) + 1)){
      //       fprintf(stderr, "rdt_output: there is enough space to proceed\n"); 
             if (conn_output(r->c, &CURRENT_WINDOW->p.data, strlen(CURRENT_WINDOW->p.data)) == -1){
        //       fprintf(stderr, "rdt_output: error sending to conn_output\n"); 
               continue; 
             }else{
          //     fprintf(stderr, "rdt_output: delivred to conn_output\n"); 
               CURRENT_WINDOW->valid = 0; // the pusher only check if it's valid push, so 
                                          // we're fine with overwriting the packet
                packet_t *temack = make_ack_pack(r); 
                conn_sendpkt(r->c, temack, ACK_HDRLEN); 
                free(temack);
               r->myackno++; //Flag that we are ready to deliver the next packet
               //CREATE AND SEND ACK PACKET HERE!
              break; 
           }
          }
        }
      }

    }
  } //TODO, REPEAT UNTIL WINDOWS ARE EMPTY!!

             // fprintf(stderr, "rdt_output: waiting for space in conn buffer \n"); 
           

           
    
 // fprintf(stderr, "rdt_output: finished loop\n"); 
}



/**
 * rdt_timer() - timer callback invoked 1/5 of the retransmission rate
 */
void rdt_timer() {
  // implement this function
  if (!rdt_list->do_timer){
    return;
  }  
  time_t currtime = time(NULL);
  for (int i = 0; i < sizeof(rdt_list->winbuf); i++){
    //print_window(rdt_list->winbuf[i], "current window:");
  if (rdt_list->winbuf[i]->len > 0){ // if a packet exists in current position
       if (rdt_list->winbuf[i]->ack != 1) {
         if (ntohl(rdt_list->winbuf[i]->p.seqno) < rdt_list->myackno){ // this packet has ALREADY been acked, and we 
                                                             // need to remove it manually. 
            rdt_list->winbuf[i]->ack = 1; 
            rdt_list->winbuf[i]->len = 0;
            continue;
         }
         //fprintf(stderr, "current difftime %f\n", difftime(currtime, rdt_list->winbuf[i]->timestamp));
         //fprintf(stderr, "and current time_jump %u\n", rdt_list->timejump); 
         if (difftime(currtime, rdt_list->winbuf[i]->timestamp) > (rdt_list->timejump / 100)){
      //     fprintf(stderr, "rdt_timer: retransmitting package\n"); 
           rdt_list->winbuf[i]->timestamp = time(NULL); 
            conn_sendpkt(rdt_list->c, &rdt_list->winbuf[i]->p, ntohs(rdt_list->winbuf[i]->p.len)); 
            rdt_list->retransmits_attempted++; 
         }
      }
    }
  }
  if (rdt_list->retransmits_attempted > 10){
    rdt_destroy(rdt_list);
  } 
  if (rdt_list->eof_recieved && are_buffers_clear(rdt_list, 0)){
    rdt_destroy(rdt_list); 
  }  
}



/* This function only gets called when the process is running as a
 * server and must handle connections from multiple clients.  You have
 * to look up the rdt_t structure based on the address in the
 * sockaddr_storage passed in.  If this is a new connection (sequence
 * number 1), you will need to allocate a new conn_t using rdt_create
 * ().  (Pass rdt_create NULL for the conn_t, so it will know to
 * allocate a new connection.)
 */
void rdt_demux(const struct config_common *cc, const struct sockaddr_storage *ss, packet_t *pkt, size_t len) {
  // ignore this function
}
