#lang br/quicklang
(require 2htdp/image)

;;; HELPERS
(define pi 3.141592653589793) ; not provided in br/quicklang
;; Canvas constants
(define CANVAS-WIDTH 500)
(define CANVAS-HEIGHT 500)
(define BLANK-CANVAS (rectangle CANVAS-WIDTH CANVAS-HEIGHT "solid" "white"))
(define (BACKGROUND colour) (rectangle CANVAS-WIDTH CANVAS-HEIGHT "solid" colour))
;; Turtle movement math
(define (next-x x angle dist) (+ x (* dist (cos angle))))
(define (next-y y angle dist) (+ y (* dist (sin angle))))
;; Draw a line between two points on an image
(define (draw-line x1 y1 x2 y2 color image)
  (add-line image x1 y1 x2 y2 color))
;; Replace element at index i in list lst with value v
(define (list-set lst i v)
  (if (= i 0)
      (cons v (cdr lst))
      (cons (car lst) (list-set (cdr lst) (- i 1) v))))
;; Read all Racket-readable tokens from a single line of text
(define (tokenize line)
  (let loop ([port (open-input-string line)] [acc '()])
    (define tok (read port))
    (if (eof-object? tok)
        (reverse acc)
        (loop port (cons tok acc)))))


;;; State = (list x y angle pen-down? color image pending)
;;;           idx: 0  1   2      3       4     5      6

; 1.1: state accessors
; state-x, state-y, state-angle, state-pen?, state-color, state-image, state-pending
#|(define (state-x state)
  (if (list? state)
      (list-ref state 0)
      "STATE MUST BE A LIST."))

(define (state-y state)
  (if (list? state)
      (list-ref state 1 )
      "STATE MUST BE A LIST."))

(define (state-angle state)
  (if (list? state)
      (list-ref state 2)
      "STATE MUST BE A LIST."))

(define (state-pen? state)
  (if (list? state)
      (list-ref state 3)
      "STATE MUST BE A LIST."))

(define (state-color state)
  (if (list? state)
      (list-ref state 4)
      "STATE MUST BE A LIST."))

(define (state-image state)
  (if (list? state)
      (list-ref state 5)
      "STATE MUST BE A LIST."))

(define (state-pending state)
  (if (list? state)
      (list-ref state 6)
      "STATE MUST BE A LIST."))|#

(define (state-x state)
  (list-ref state 0))
(define (state-y state)
  (list-ref state 1))
(define (state-angle state)
  (list-ref state 2))
(define (state-pen? state)
  (list-ref state 3))
(define (state-color state)
  (list-ref state 4))
(define (state-image state)
  (list-ref state 5))
;(define (state-pen state)
 ; (list-ref state 6)) 
;(define (state-pending state)
 ; (list-ref state 6))

; tests
#|(define test-s (list 10 20 0 #t "blue" BLANK-CANVAS))

; tests
(state-x test-s)     ; => 10
(state-y test-s)     ; => 20
(state-angle test-s) ; => 0
(state-pen? test-s)  ; => #t
(state-color test-s) ; => "blue"
(state-image test-s) ; => BLANK-CANVAS
|#
; 1.2: state updaters
; set-x, set-y, set-angle, set-pen, set-color, set-image, set-pending

;(define (set- state val)
 ;(list (state-x) (state-y state) (state-angle state) (state-pen? state) (state-color state) (state-image state) (state-pending state)))
(define (set-x state val)
  (list val (state-y state) (state-angle state) (state-pen? state) (state-color state) (state-image state) ))

(define (set-y state val)
  (list (state-x state) val (state-angle state) (state-pen? state) (state-color state) (state-image state) ))

(define (set-angle state val)
 (list (state-x state) (state-y state) val (state-pen? state) (state-color state) (state-image state) ))

(define (set-pen state val)
 (list (state-x state) (state-y state) (state-angle state) val (state-color state) (state-image state)))

(define (set-color state val)
 (list (state-x state) (state-y state) (state-angle state) (state-pen? state) val (state-image state)))
(define (set-image state val)
 (list (state-x state) (state-y state) (state-angle state) (state-pen? state) (state-color state) val))
;(define (set-pen state val)
 ; (list-set state 6 val)) 


;(define (set-pending state val)
 ;(list (state-x state) (state-y state) (state-angle state) (state-pen? state) (state-color state) (state-image state) val))

; tests
#|; --- Each updater changes only its field ---
(state-x     (set-x test-s 99))          ; => 99
(state-y     (set-y test-s 99))          ; => 99
(state-angle (set-angle test-s pi))      ; => pi
(state-pen?  (set-pen test-s #f))        ; => #f
(state-color (set-color test-s "green")) ; => "green"

; --- Updating one field must not disturb the others ---
; set-x: y, angle, pen, color should be unchanged
(state-y     (set-x test-s 99)) ; => 20     (unchanged)
(state-angle (set-x test-s 99)) ; => 0      (unchanged)
(state-color (set-x test-s 99)) ; => "blue" (unchanged)

; set-pen: x, y, color should be unchanged
(state-x     (set-pen test-s #f)) ; => 10     (unchanged)
(state-color (set-pen test-s #f)) ; => "blue" (unchanged)

; set-color: pen should be unchanged
(state-pen? (set-color test-s "green")) ; => #t (unchanged)

; --- Updating the same field twice: last write wins ---
(state-x (set-x (set-x test-s 99) 42)) ; => 42

; --- Changing the image changes the image ---
(state-image (set-image test-s (circle 30 "solid" "green")))
|#
; 1.3: initial-state
; turtle at canvas center, pointing up (angle = -(pi/2)), pen up, color "black", no pending arg
(define initial-state
  (list (/ CANVAS-WIDTH 2) (/ CANVAS-HEIGHT 2) (- (/ pi 2)) #f "black" BLANK-CANVAS))

#|; tests
(state-x initial-state)     ; => 250  (center of 500x500 canvas)
(state-y initial-state)     ; => 250
(state-angle initial-state) ; => (- (/ pi 2))  (pointing upward)
(state-pen? initial-state)  ; => #f
(state-color initial-state) ; => "black"
|#
;;; Part 2: Reading the Program

;(-line 50 50 100 100 "black" BLANK-CANVAS)

(define (filter-helper inp)
  (not
    (or 
      (void? inp) 
      (or
        (null? inp)
        (or
          (string-prefix? inp ";")
          (or
            (string-prefix? inp " ")
            (or
              (string=? inp "")
              (string-prefix? inp "#"))))))))
            
    



;; THE READER
;; read-syntax is called by Racket when a file beginning with #lang "project4.rkt" is opened.
;; Complete the two missing definitions below.
(define (read-syntax path port)
  ;(displayln port) 
  (define src-lines (port->lines port))
  ;(displayln src-lines)
  (define filtered (filter filter-helper src-lines))
  (displayln filtered) ; filter out blank lines and lines starting with ";
  (define src-datums (map (lambda (x) `',(tokenize x)) filtered)) ;ll(map (lambda (x)  filtered)) ; tokenize each filtered line, then flatten into one list
  ;(displayln src-datums)
    (define module-datum
    `(module turtle-mod "project4.rkt"
       (handle-turtle-cmds ,@src-datums)))
  (datum->syntax #f module-datum))
(provide quote read-syntax)


;; THE EXPANDER
;; module-begin: calls handle-turtle-cmds, extracts the final image, displays it
(define-macro (turtle-module-begin EXPR)
  #'(#%module-begin
     (display (state-image EXPR))))
(provide (rename-out [turtle-module-begin #%module-begin]))


;;; Part 3: Command Dispatch

;(define (new-pen state [pen-width 2] [pen-style "solid"] [pen-cap "round"] [bevel "join"])
 ;' (make-pen (state-color state) pen-width pen-style pen-cap bevel)) 
; 3.1: handle-cmd
; Dispatch on: number, FORWARD, BACK, RIGHT, LEFT, PENDOWN, PENUP
; Return the updated state for each case.

(define (handle-cmd state cmd)
  (define name (first cmd))
  (cond
    [(equal? name 'SETPOS) (set-y (set-x state (second cmd)) (third cmd))]
    [(equal? name 'FORWARD) (let* ([new-x (next-x (state-x state) (state-angle state) (second cmd))]
                              [new-y (next-y (state-y state) (state-angle state) (second cmd))]
                              [new-image (draw-line (state-x state) (state-y state) new-x new-y (state-color state) (state-image state))])
                              (if state-pen?                   
                                    (set-image (set-y (set-x state new-x) new-y) new-image)
                              (displayln "PEN NOT DOWN" )))] ;REMEMBER VARIABLES ARE IMMUTABLE
    
     #|(begin ;(displayln "trying to print image")
                               (if state-pen? ;(set-image state 
                                                         (draw-line (state-x state) (state-y state) (next-x (state-x state) (state-angle state) (second cmd))
                                                       (next-y (state-y state) (state-angle state) (second cmd)) (state-color state) (state-image state))
                                   (displayln "PEN NOT DOWN"))
                               (set-x state (next-x (state-x state) (state-angle state) (second cmd)))
                               (set-y state (next-y (state-y state) (state-angle state) (second cmd))))]  ; (second cmd) is the distance |#
    [(equal? name 'BACK)(let* ([new-x (next-x (state-x state) ( - (state-angle state)) (second cmd))]
                              [new-y (next-y (state-y state) ( - (state-angle state)) (second cmd))]
                              [new-image (draw-line (state-x state) (state-y state) new-x new-y (state-color state) (state-image state))])
                              (if state-pen?                   
                                    (set-image (set-y (set-x state new-x) new-y) new-image)
                              (displayln "PEN NOT DOWN" )))]
        [(equal? name 'COLOR) (set-color state (symbol->string (second cmd)))]
    [(equal? name 'RIGHT) (set-angle state (+ (state-angle state) (* (second cmd) (/ pi 180))))]     ; (second cmd) is the degrees
    [(equal? name 'LEFT) (set-angle state (- (state-angle state) (* (second cmd) (/ pi 180))))]
    [(equal? name 'PENDOWN) (set-pen state #t)]
    [(equal? name 'PENUP) (set-pen state #f)]
    [(equal? name 'BACKCOLOR) (set-image state (BACKGROUND (symbol->string (second cmd))))]
    ;[(equal? name 'CHANGEPEN) 
    
    [else state]))                 ; unknown command: ignore

; tests


; Tests (run these against your implementation):
(state-pen? (handle-cmd initial-state '(PENDOWN))) ; => #t
(state-pen? (handle-cmd initial-state '(PENUP)))   ; => #f

; After PENDOWN then FORWARD 100, turtle should move up 100 pixels:
;(define s (handle-cmd initial-state '(COLOR "blue")))
(define s1 (handle-cmd initial-state '(PENDOWN)))
(define s2 (handle-cmd s1 '(FORWARD 100)))
(state-x s2) ; => 250.0  (x unchanged when heading straight up)
(state-y s2) ; => 150.0  (moved up 100 pixels)

; RIGHT 90 should rotate the angle by pi/2:
(define s3 (handle-cmd initial-state '(RIGHT 90)))
(state-angle s3) ; => 0.0  (was -(pi/2), added pi/2, now 0 = pointing right)

; test the image changes:
(state-image s2) ; should have a line drawn on it!
(draw-line 20 20 49 49 (state-color s2) (state-image s2))
;|#

; 3.2: handle-turtle-cmds
; Use for/fold to process all tokens left to right, starting from initial-state.
; (This is the direct parallel to handle-args in the funstacker example.)

(define (handle-turtle-cmds . cmds) (for/fold
                                       ([curr initial-state])
                                       ([cmd (in-list cmds)])
                                        (handle-cmd curr cmd)))
(provide handle-turtle-cmds)

; tests (write a .turtle file and run it!)


;;; Part 4: Extensions

; 4.1: COLOR
; Add a COLOR command. Decide how to handle color-name symbols in handle-cmd.

; 4.2: BACK and SETPOS

; 4.3: REPEAT (stretch goal)
; expand-repeats: list of tokens -> list of tokens with REPEAT...END blocks expanded

#|(define (expand-repeats tokens)
  (define (expand tokens acc)
    (if (equal? (car tokens) 'END)
        acc
        (cons |#

;;; Part 5: Your Logo Program
;;; Write your program in a separate .turtle file.

