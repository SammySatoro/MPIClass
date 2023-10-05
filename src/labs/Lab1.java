package labs;

import mpi.MPI;

public class Lab1 {
    public Lab1(String[] args) {
        int myrank, size, message;
        int TAG = 0;
        MPI.Init(args);
        myrank = MPI.COMM_WORLD.Rank();
        size = MPI.COMM_WORLD.Size();
        message = myrank;
        if ((myrank % 2) == 0) {
            if ((myrank + 1) != size) {
                MPI.COMM_WORLD.Send(new int[]{message}, 0, 1, MPI.INT, myrank + 1, TAG);
            }
        } else {
            MPI.COMM_WORLD.Recv(new int[1], 0, 1, MPI.INT, myrank - 1, TAG);
            System.out.println("received: " + message);
        }
        MPI.Finalize();
    }

}
