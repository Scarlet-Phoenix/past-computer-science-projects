public class OverflowExample {
    public static void main(String[] args)
    {
        System.out.println("Int overflow demo");
        int x = 2147483646;
        System.out.println(x);
        x++;
        System.out.println(x);
        x++;
        System.out.println(x);
        x++;
        System.out.println(x);

        System.out.println("Long overflow demo");
        long y = 9223372036854775806L;
        System.out.println(y);
        y++;
        System.out.println(y);
        y++;
        System.out.println(y);
        y++;
        System.out.println(y);



    }
}
