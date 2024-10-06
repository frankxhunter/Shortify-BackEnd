package com.shortify.utils;

import org.mindrot.jbcrypt.BCrypt;

public class Security {

    public static String encriptation(String data){
        return BCrypt.hashpw(data, BCrypt.gensalt());
    }

    public static boolean desEncriptationComparation(String data, String dataHashed){
        return BCrypt.checkpw(data, dataHashed);
    }
    
}
