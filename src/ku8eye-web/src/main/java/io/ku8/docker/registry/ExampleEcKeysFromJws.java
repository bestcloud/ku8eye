package io.ku8.docker.registry;
import java.math.BigInteger;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

import org.apache.commons.logging.LogFactory;
import org.jose4j.keys.BigEndianBigInteger;
import org.jose4j.keys.EcKeyUtil;
import org.jose4j.keys.EllipticCurves;
import org.jose4j.lang.ByteUtil;
import org.jose4j.lang.JoseException;

public class ExampleEcKeysFromJws
{
    // The ECDSA key consists of a public part, the EC point (x, y)
    public static final int[] X_INTS_256 = {127, 205, 206, 39, 112, 246, 196, 93, 65, 131, 203,
        238, 111, 219, 75, 123, 88, 7, 51, 53, 123, 233, 239,
        19, 186, 207, 110, 60, 123, 209, 84, 69};
    public static final int[] Y_INTS_256 =  {199, 241, 68, 205, 27, 189, 155, 126, 135, 44, 223,
        237, 185, 238, 185, 244, 179, 105, 93, 110, 169, 11,
        36, 173, 138, 70, 35, 40, 133, 136, 229, 173};

    // and a private part d.
    public static final int[] D_INTS_256 = {142, 155, 16, 158, 113, 144, 152, 191, 152, 4, 135,
        223, 31, 93, 119, 233, 203, 41, 96, 110, 190, 210,
        38, 59, 95, 87, 194, 19, 223, 132, 244, 178};

    public static final byte[] X_BYTES_256 = ByteUtil.convertUnsignedToSignedTwosComp(X_INTS_256);
    public static final byte[] Y_BYTES_256 = ByteUtil.convertUnsignedToSignedTwosComp(Y_INTS_256);
    public static final byte[] D_BYTES_256 = ByteUtil.convertUnsignedToSignedTwosComp(D_INTS_256);

    public static final BigInteger X_256 = BigEndianBigInteger.fromBytes(X_BYTES_256);
    public static final BigInteger Y_256 = BigEndianBigInteger.fromBytes(Y_BYTES_256);
    public static final BigInteger D_256 = BigEndianBigInteger.fromBytes(D_BYTES_256);


    public static ECPrivateKey PRIVATE_256 = null;
    public static ECPublicKey PUBLIC_256 = null;


        // The ECDSA key consists of a public part, the EC point (x, y)
    public static final int[] X_INTS_521 = {1, 233, 41, 5, 15, 18, 79, 198, 188, 85, 199, 213,
        57, 51, 101, 223, 157, 239, 74, 176, 194, 44, 178,
        87, 152, 249, 52, 235, 4, 227, 198, 186, 227, 112,
        26, 87, 167, 145, 14, 157, 129, 191, 54, 49, 89, 232,
        235, 203, 21, 93, 99, 73, 244, 189, 182, 204, 248,
        169, 76, 92, 89, 199, 170, 193, 1, 164};
    public static final int[] Y_INTS_521 = {0, 52, 166, 68, 14, 55, 103, 80, 210, 55, 31, 209,    
        189, 194, 200, 243, 183, 29, 47, 78, 229, 234, 52,
        50, 200, 21, 204, 163, 21, 96, 254, 93, 147, 135,
        236, 119, 75, 85, 131, 134, 48, 229, 203, 191, 90,
        140, 190, 10, 145, 221, 0, 100, 198, 153, 154, 31,
        110, 110, 103, 250, 221, 237, 228, 200, 200, 246};

    // and a private part d.
    public static final int[] D_INTS_521 = {1, 142, 105, 111, 176, 52, 80, 88, 129, 221, 17, 11,
        72, 62, 184, 125, 50, 206, 73, 95, 227, 107, 55, 69,
        237, 242, 216, 202, 228, 240, 242, 83, 159, 70, 21,
        160, 233, 142, 171, 82, 179, 192, 197, 234, 196, 206,
        7, 81, 133, 168, 231, 187, 71, 222, 172, 29, 29, 231,
        123, 204, 246, 97, 53, 230, 61, 130};

    public static final byte[] X_BYTES_521 = ByteUtil.convertUnsignedToSignedTwosComp(X_INTS_521);
    public static final byte[] Y_BYTES_521 = ByteUtil.convertUnsignedToSignedTwosComp(Y_INTS_521);
    public static final byte[] D_BYTES_521 = ByteUtil.convertUnsignedToSignedTwosComp(D_INTS_521);

    public static final BigInteger X_521 = BigEndianBigInteger.fromBytes(X_BYTES_521);
    public static final BigInteger Y_521 = BigEndianBigInteger.fromBytes(Y_BYTES_521);
    public static final BigInteger D_521 = BigEndianBigInteger.fromBytes(D_BYTES_521);


    public static ECPrivateKey PRIVATE_521 = null;
    public static ECPublicKey PUBLIC_521 = null;

    static
    {
        EcKeyUtil ecKeyUtil = new EcKeyUtil();

        try
        {
            PRIVATE_256 = ecKeyUtil.privateKey(D_256, EllipticCurves.P256);
            PUBLIC_256 = ecKeyUtil.publicKey(X_256, Y_256, EllipticCurves.P256);

            PRIVATE_521 = ecKeyUtil.privateKey(D_521, EllipticCurves.P521);
            PUBLIC_521 = ecKeyUtil.publicKey(X_521, Y_521, EllipticCurves.P521);
        }
        catch (JoseException e)
        {
            LogFactory.getLog(ExampleEcKeysFromJws.class).warn("Unable to initialize Example EC keys.", e);
        }
    }
}