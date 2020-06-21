package consumer_data_privacy_hba;



class A

{

   public static void findCombination(String prefix, String remaining)

   {

      if (remaining.length() > 0)

      {

         String newRemaining = remaining.substring(1);

         String newPrefix = prefix + remaining.charAt(0);

         findCombination(newPrefix, newRemaining);

         findCombination(prefix, newRemaining);

      }

      else

      {

         System.out.println(prefix);

      }

   }

   

   public static void main(String[] args)

   {

      findCombination("", "ABC");

   }

}