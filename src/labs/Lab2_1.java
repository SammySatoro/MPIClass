package labs;
import mpi.*;

public class Lab2_1 {
    public Lab2_1(String[] args) {
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int message = myrank;
        int[] buf = new int[1];
        int TAG = 0;

        if (myrank == 0)
        {
            MPI.COMM_WORLD.Send(new int[]{message},0,1, MPI.INT,myrank + 1,TAG);
            System.out.println("Sent package:" + myrank);

            MPI.COMM_WORLD.Recv(buf,0, 1, MPI.INT, size - 1, TAG);

            System.out.println("Accepted package:" + myrank);
            System.out.println("Total sum: " + buf[0]);
        }
        else {
            MPI.COMM_WORLD.Recv(buf,0, 1, MPI.INT, myrank - 1, TAG);
            message += buf[0];
            System.out.println("Accepted package:" + myrank);

            MPI.COMM_WORLD.Send(new int[]{message},0,1,MPI.INT,(myrank + 1 + size) % size,TAG);
            System.out.println("Sent package:" + myrank);
        }
        MPI.Finalize();
    }
}
