package labs;
import mpi.*;

public class Lab2_1 {
    public Lab2_1(String[] args) {
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int s = myrank;
        int[] buf = new int[1];
        int TAG = 0;

        if (myrank == 0)
        {
            MPI.COMM_WORLD.Send(new int[]{s},0,1,MPI.INT,myrank + 1,TAG);
            System.out.println("sent the package:" + myrank);

            MPI.COMM_WORLD.Recv(buf,0, 1, MPI.INT, size - 1, TAG);

            System.out.println("accepted the package:" + myrank);
            System.out.println("Total sum: " + buf[0]);
        }
        else {
            MPI.COMM_WORLD.Recv(buf,0, 1, MPI.INT, myrank - 1, TAG);
            s += buf[0];
            System.out.println("accepted the package:" + myrank);

            MPI.COMM_WORLD.Send(new int[]{s},0,1,MPI.INT,(myrank + 1 + size) % size,TAG);
            System.out.println("sent the package:" + myrank);
        }
        MPI.Finalize();
    }
}
