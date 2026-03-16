import java.time.Instant;
import java.util.Random;
import java.util.Scanner;
public class CardGame {


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        RotatingArrayList<Integer> testList = new RotatingArrayList<Integer>(); 
        RotatingArrayList<Integer> playList = new RotatingArrayList<Integer>(); 

        // Write your game here.  Add any functions you want.

        System.out.println("How many numbers do you want in your game? ");
        int grabLim = scanner.nextInt();
        for (int i = 1; i <= grabLim; i++)
        {
            testList.append(i);
            playList.append(i);
        }
        randomize(playList); 
        System.out.println(playList.toString());
        System.out.println(testList.toString());
        int select; 
        int cardHolder = 0; 
        while (!(testList.equals(playList)))
        {
            System.out.println("Holding :" + cardHolder);
            System.out.println("Deck: " +  playList.toString());

            System.out.println("Select an option. \n1: rotate left, \n2: rotate right \n3: append, \n4: prepend, \n5: removeFirst, \n6: removeLast");
            select = scanner.nextInt();
            switch (select){
                case 1:
                    playList.rotateLeft();
                    continue;
                case 2:
                    playList.rotateRight();
                    continue;
                case 3: 
                    playList.append(cardHolder);
                    continue;
                case 4:
                    playList.prepend(cardHolder);
                    continue;
                case 5: 
                    cardHolder = playList.get(0);
                    playList.removeFirst();
                    continue;
                case 6:
                    cardHolder = playList.grabLast();
                    playList.removeLast();
                    continue;
            }
        }
        System.out.println("Connection terminated. I'm sorry to interrupt you, Elizabeth, if you still even remember that name, But I'm afraid you've been misinformed.\n You are not here to receive a gift, nor have you been called here by the individual you assume,\n although, you have indeed been called. You have all been called here,\n into a labyrinth of sounds and smells, misdirection and misfortune\n. A labyrinth with no exit, a maze with no prize. You don't even realize that you are trapped.\n Your lust for blood has driven you in endless circles, chasing the cries of children in some unseen chamber,\n always seeming so near, yet somehow out of reach,\n but you will never find them. None of you will.\n This is where your story ends. And to you, my brave volunteer,\n who somehow found this job listing not intended for you, although there was a way out planned for you,\n I have a feeling that's not what you want. I have a feeling that you are right where you want to be. I am remaining as well.\n I am nearby. This place will not be remembered,\n and the memory of everything that started this can finally begin to fade away. As the agony of every tragedy\n should. And to you monsters trapped in the corridors, be still and give up your spirits. They don't belong to you. \nFor most of you, I believe there is peace and perhaps more waiting for you after the smoke clears. Although, for one of \nyou, the darkest pit of Hell has opened to swallow you whole, so don't keep the devil waiting, old friend. My daughter, if you can\n hear me, I knew you would return as well. It's in your nature to protect the innocent. I'm\n sorry that on that day, the day you were shut out and left to die, no one was there to lift you\n up into their arms the way you lifted others into yours, and then, what became of you. I should have known\n you wouldn't be content to disappear, not my daughter. I couldn't save you then, so let me save you now. It's time to rest - for you,\n and for those you have carried in your arms. This ends for all of us. End communication. ");

    }


    static void randomize(RotatingArrayList<Integer> arr)
    {
        Random randomizer = new Random(Instant.now().toEpochMilli()); 
        for (int counter = arr.size() - 1; counter > 0; counter--)
        {
            int pickRand = randomizer.nextInt(counter + 1); 

            Integer temp = arr.get(counter);
            arr.set(counter, arr.get(pickRand)); 
            arr.set(pickRand, temp); 
        }
    }

}
