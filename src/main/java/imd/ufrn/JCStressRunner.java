package imd.ufrn;

public class JCStressRunner {
    public static void main(String[] args) throws Exception {
        org.openjdk.jcstress.Main.main(new String[]{"-v", "-t", "imd.ufrn.jcstress"});
    }
}
