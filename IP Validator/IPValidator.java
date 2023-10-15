public class IPValidator
{
    public static boolean validateIPv4Address(String str){
        String[] octets = str.split("\\.");
        if (octets.length != 4)
            return false;

        for (int i = 0;i< octets.length;i++){
            if(octets[i].charAt(0) == '0')
                return false;

            for (int j = 0;j< octets[i].length();j++){

                if(!Character.isDigit(octets[i].charAt(j))){
                    return false;
                }

            }
            if (Integer.parseInt(octets[i])<0 || Integer.parseInt(octets[i])>255){
                return false;
            }

        }
        return true;


    }
}
