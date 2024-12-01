package dev.bannmann.labs.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.experimental.UtilityClass;

import com.github.mizool.core.exception.CodeInconsistencyException;

@UtilityClass
public class StandardMessageDigests
{
    public static final MessageDigest SHA3_256;

    static
    {
        try
        {
            SHA3_256 = MessageDigest.getInstance("SHA3-256");
        }
        catch (NoSuchAlgorithmException e)
        {
            // SHA3-256 is specified to be available in any JDK 9+ JVM.
            throw new CodeInconsistencyException(e);
        }
    }
}
