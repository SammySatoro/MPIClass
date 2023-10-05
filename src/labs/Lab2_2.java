package labs;
import mpi.*;

public class Lab2_2 {
    public Lab2_2(String[] args) {
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int s = myrank;
        int[] buf = new int[1];
        int TAG = 0;
        Request sendRequest;
        Request receiveRequest;

        if (myrank ==0)
        {
            sendRequest = MPI.COMM_WORLD.Isend(new int[]{s}, 0, 1, MPI.INT, (myrank + 1) % size, TAG);
            System.out.println("sent the package:" + myrank);
            sendRequest.Wait();


            receiveRequest = MPI.COMM_WORLD.Irecv(buf, 0, 1, MPI.INT, (myrank - 1 + size) % size, TAG);
            receiveRequest.Wait();

            System.out.println("accepted the package:" + myrank);
            System.out.println("Total sum: " + buf[0]);
        }
        else {
            receiveRequest = MPI.COMM_WORLD.Irecv(buf, 0, 1, MPI.INT, (myrank - 1 + size) % size, TAG);
            receiveRequest.Wait();s +=buf[0];
            System.out.println("accepted the package:" + myrank);

            sendRequest = MPI.COMM_WORLD.Isend(new int[]{s}, 0, 1, MPI.INT, (myrank + 1) % size, TAG);
            System.out.println("sent the package:" + myrank);
            sendRequest.Wait();
        }
        MPI.Finalize();
    }
}
