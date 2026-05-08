package ca.jrvs.apps.practice;

public interface RegexExc {

  /**
   * return true if filename extension is jpg or jpeg (case-insensitive)
   * @param filename (name of file)
   * @return boolean
   */
   boolean matchJpeg(String filename);

  /**
   * return true if ip is valid
   * to simplify the problem, IP address range is 0.0.0.0 - 999.999.999.999
   * @param ip (ip address)
   * @return boolean
   */
  boolean matchIp(String ip);

  /**
   * return true if line is empty(ie empty, whitespace, tabs etc...)
   * @param line (line to check)
   * @return boolean
   */
   boolean isEmptyLine(String line);
}
