import system.TrailManager;

public class Main {
    public static void main(String[] args) {
        TrailManager trailManager = TrailManager.getTrailManager();
        trailManager.initialize("./Test_files/pigeonholes/pigeonhole7.cnf");

        long start = System.currentTimeMillis();

        trailManager.unitClausePropagation();

        while(true) {
            if(trailManager.isSat()) {
                System.out.println("\nThe problem is SAT.\nModel: " + TrailManager.getTrailManager().getTrail());
                break;
            }

            if(trailManager.canPropagate())
                trailManager.propagate();
            else
                trailManager.decide();
        }

        System.out.println("\nThe problem was solved in " + (System.currentTimeMillis() - start) + "ms");
    }
}
