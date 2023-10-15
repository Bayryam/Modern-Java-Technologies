public class JumpGame {

    private static boolean canWinRecurs(int[] array, int startIndex){
        if (startIndex == array.length-1){
            return true;
        }
        int steps = array[startIndex];
        boolean result = false;
        for(int i = 1;i<=steps;i++){
            result = result || canWinRecurs(array,i+startIndex);
        }

        return result;


    }
    public static boolean canWin(int[] array){
        return canWinRecurs(array,0);
    }
}
