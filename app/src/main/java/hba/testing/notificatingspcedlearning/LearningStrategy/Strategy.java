package hba.testing.notificatingspcedlearning.LearningStrategy;

import java.util.Random;

/**
 * Created by TranKim on 12/27/2016.
 */
public final class Strategy {
    //All are in mili Second
    private static final long doubleSpacedTimeLearningInitialInterval=10*24*3600*1000;
    private static final long FIRST_REMIND_RANDOM_RANGE=2;
//    private static final long[] duration={1,2,3};
    private static final long[] duration={24*3600*1000,7*24*3600*1000};
    private static long sqr(int pow){
        long tmp=1;
        for(int i=0;i<pow;i++)
            tmp*=2;
        return tmp;
    }
    public static final long doubleSpacedTimeLearning(int currentLevel){
        switch(currentLevel){
            case 0:
            case 1:
                return duration[currentLevel];
            default:
                return sqr(currentLevel-1)*duration[1];
        }
    }

    /**
     * Randomize the first remind for the item
     * The remind first showing day will be set randomly within 1 week from the day the item created
     * @return the time for the first remind
     */
    public static final long randomFirstRemind(){
        return ((long)(Math.ceil(Math.random()*FIRST_REMIND_RANDOM_RANGE+1)))*24*3600*1000;
    }
    public static final long olddoubleSpacedTimeLearning(int currentLevel){
        switch(currentLevel){
            case 0:
                Random tmp=new Random();
                /*make the item randomly showed in the first appearance. This is to keep user from getting
                familiar with the displaying order of the word and prevent word with similar meaning from appearing next to each other
                 */
                return ((tmp.nextInt(13)+1)*duration[0])*1000;
            case 1:
            case 2:
                return duration[currentLevel]*1000;
            default:
                return sqr(currentLevel-2)*duration[2]*1000;
        }
    }
}
