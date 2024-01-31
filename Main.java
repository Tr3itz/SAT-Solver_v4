import system.TrailManager;

public class Main {
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Please, run the file providing the path to a .cnf file.");
            System.exit(0);
        } else if(args.length > 1){
            System.out.println("Too many arguments.");
            System.exit(0);
        }

        TrailManager trailManager = TrailManager.getTrailManager();
        trailManager.initialize(args[0]);

        long start = System.currentTimeMillis();

        trailManager.unitClausePropagation();

        while(true) {
            if(trailManager.isUnsat()) {
                System.out.println("\nThe problem is UNSAT.");
                break;
            }

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
