import java.util.Arrays;

public class BrokenKeyboard
{
    public static int calculateFullyTypedWords(String message, String brokenKeys){
        int fullWords = 0;
        String[] words = Arrays.stream(message.split(" ")).filter(w -> w != "").toArray(String[]::new);
        for (String word : words) {
            boolean isFullWord = true;
            for (int j = 0; j < brokenKeys.length(); j++) {
                if (word.contains(String.valueOf(brokenKeys.charAt(j)))) {
                    isFullWord = false;
                    break;
                }
            }

            if (isFullWord)
                fullWords++;

        }
        return fullWords;
    }
}
