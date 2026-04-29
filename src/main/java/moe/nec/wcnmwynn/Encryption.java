package moe.nec.wcnmwynn;

import com.google.common.collect.Lists;
import lombok.val;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

public class Encryption {
  private static final String SERVER_IP_CHARSET = generateCharset(0);

  private static String generateCharset(long seed) {
    val characters = "0123456789-.:[qwertyuiopasdfghjklzxcvbnm";
    val characterList = new ArrayList<>(Lists.charactersOf(characters));
    val random = new Random(0x114514_ACCA2C_6978L ^ seed);
    val builder = new StringBuilder("]");
    while (!characterList.isEmpty()) {
      builder.append(characterList.remove(random.nextInt(characterList.size())));
    }
    return builder.toString();
  }

  private static BigInteger base41(String charset, String ip) {
    var base41 = BigInteger.ZERO;
    val big41 = BigInteger.valueOf(41);
    for (val ch : ip.toCharArray()) {
      val index = charset.indexOf(ch);
      if (index == -1) return null;
      base41 = base41.multiply(big41).add(BigInteger.valueOf(index));
    }
    return base41;
  }

  private static String debase41(String charset, BigInteger encrypted) {
    val big41 = BigInteger.valueOf(41);
    val builder = new StringBuilder();
    while (!BigInteger.ZERO.equals(encrypted)) {
      val dar = encrypted.divideAndRemainder(big41);
      encrypted = dar[0];
      builder.insert(0, charset.charAt(dar[1].intValue()));
    }
    return builder.toString();
  }

  public static String encrypt(String boostIp, String serverIp) {
    val serverIp41 = base41(SERVER_IP_CHARSET, serverIp);
    if (serverIp41 == null) return null;
    val boostIp41 = base41(
      generateCharset(serverIp41.longValue()),
      boostIp
    );
    if (boostIp41 == null) return null;
    return boostIp41.toString(36);
  }

  public static String decrypt(String encrypted, String serverIp) {
    val serverIp41 = base41(SERVER_IP_CHARSET, serverIp);
    if (serverIp41 == null) return null;
    return debase41(
      generateCharset(serverIp41.longValue()),
      new BigInteger(encrypted, 36)
    );
  }
}
