package com.socket;

import java.io.IOException;
import java.net.*;


public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);

        //bind
        serverSocket.bind(new InetSocketAddress(8888));

        // 等待客户端连接
        Socket client = serverSocket.accept();

        InetAddress clientAddress = client.getInetAddress();

        String ip = toAddressString(clientAddress,true);

        System.out.println(clientAddress.toString() + ":" + client.getPort() + "::" + client.getLocalPort());
    }

    public static String toAddressString(InetAddress ip, boolean ipv4Mapped) {
        if (ip instanceof Inet4Address) {
            return ip.getHostAddress();
        }
        if (!(ip instanceof Inet6Address)) {
            throw new IllegalArgumentException("Unhandled type: " + ip);
        }

        return toAddressString(ip.getAddress(), 0, ipv4Mapped);
    }

    private static String toAddressString(byte[] bytes, int offset, boolean ipv4Mapped) {
        final int[] words = new int[8];
        int i;
        final int end = offset + words.length;
        for (i = offset; i < end; ++i) {
            words[i] = ((bytes[i << 1] & 0xff) << 8) | (bytes[(i << 1) + 1] & 0xff);
        }

        // Find longest run of 0s, tie goes to first found instance
        int currentStart = -1;
        int currentLength;
        int shortestStart = -1;
        int shortestLength = 0;
        for (i = 0; i < words.length; ++i) {
            if (words[i] == 0) {
                if (currentStart < 0) {
                    currentStart = i;
                }
            } else if (currentStart >= 0) {
                currentLength = i - currentStart;
                if (currentLength > shortestLength) {
                    shortestStart = currentStart;
                    shortestLength = currentLength;
                }
                currentStart = -1;
            }
        }
        // If the array ends on a streak of zeros, make sure we account for it
        if (currentStart >= 0) {
            currentLength = i - currentStart;
            if (currentLength > shortestLength) {
                shortestStart = currentStart;
                shortestLength = currentLength;
            }
        }
        // Ignore the longest streak if it is only 1 long
        if (shortestLength == 1) {
            shortestLength = 0;
            shortestStart = -1;
        }

        // Translate to string taking into account longest consecutive 0s
        final int shortestEnd = shortestStart + shortestLength;
        final StringBuilder b = new StringBuilder(39);
        if (shortestEnd < 0) { // Optimization when there is no compressing needed
            b.append(Integer.toHexString(words[0]));
            for (i = 1; i < words.length; ++i) {
                b.append(':');
                b.append(Integer.toHexString(words[i]));
            }
        } else { // General case that can handle compressing (and not compressing)
            // Loop unroll the first index (so we don't constantly check i==0 cases in loop)
            final boolean isIpv4Mapped;
            if (inRangeEndExclusive(0, shortestStart, shortestEnd)) {
                b.append("::");
                isIpv4Mapped = ipv4Mapped && (shortestEnd == 5 && words[5] == 0xffff);
            } else {
                b.append(Integer.toHexString(words[0]));
                isIpv4Mapped = false;
            }
            for (i = 1; i < words.length; ++i) {
                if (!inRangeEndExclusive(i, shortestStart, shortestEnd)) {
                    if (!inRangeEndExclusive(i - 1, shortestStart, shortestEnd)) {
                        // If the last index was not part of the shortened sequence
                        if (!isIpv4Mapped || i == 6) {
                            b.append(':');
                        } else {
                            b.append('.');
                        }
                    }
                    if (isIpv4Mapped && i > 5) {
                        b.append(words[i] >> 8);
                        b.append('.');
                        b.append(words[i] & 0xff);
                    } else {
                        b.append(Integer.toHexString(words[i]));
                    }
                } else if (!inRangeEndExclusive(i - 1, shortestStart, shortestEnd)) {
                    // If we are in the shortened sequence and the last index was not
                    b.append("::");
                }
            }
        }

        return b.toString();
    }

    private static boolean inRangeEndExclusive(int value, int start, int end) {
        return value >= start && value < end;
    }

}
