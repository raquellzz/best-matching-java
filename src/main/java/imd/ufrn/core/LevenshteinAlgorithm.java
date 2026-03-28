package imd.ufrn.core;

public class LevenshteinAlgorithm {

    /**
     * Calcula a Distância de Levenshtein entre duas strings.
     * @param a A primeira palavra (ex: palavra-alvo da busca)
     * @param b A segunda palavra (ex: palavra lida do texto)
     * @return O número mínimo de edições para transformar 'a' em 'b'
     */
    public static int calculate(String a, String b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("As strings não podem ser nulas");
        }
        int lengthA = a.length();
        int lengthB = b.length();

        int[][] dp = new int[lengthA + 1][lengthB + 1];

        for (int i = 0; i <= lengthA; i++) {
            for (int j = 0; j <= lengthB; j++) {
                if (i == 0) {
                    dp[i][j] = j; 
                }
                else if (j == 0) {
                    dp[i][j] = i; 
                }
                else {
                    int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(Math.min(
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + cost
                    );
                }
            }
        }
        return dp[lengthA][lengthB];
    }
}