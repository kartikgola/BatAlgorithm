
import java.util.Random;
import java.util.Arrays;

public class BatAlgorithm {

	private double[][] X; 		// Population/Solution (N x D)
	private double[][] V; 		// Velocities (N x D)
	private double[][] Q; 		// Frequency : 0 to Q_MAX (N x 1)
	private double[] F;			// Fitness (N)
	private double R; 			// Pulse Rate : 0 to 1
	private double A; 			// Louadness : A_MIN to A_MAX
	private double[][] lb;		// Lower bound (1 x D)
	private double[][] ub;		// Upper bound (1 x D)
	private double fmin; 		// Minimum fitness from F
 	private double[] B;			// Best solution array from X (D)	

	private final int N; 		// Number of bats
	private final int MAX; 		// Number of iterations
	private final double Q_MIN = 0.0;
	private final double Q_MAX = 2.0;
	private final double A_MIN;
	private final double A_MAX;
	private final double R_MIN;
	private final double R_MAX; 
	private final int D = 10;
	private final Random rand = new Random();

	public BA(int N, int MAX, double A_MIN, double A_MAX, double R_MIN, double R_MAX){
		this.N = N;
		this.MAX = MAX;
		this.R_MAX = R_MAX;
		this.R_MIN = R_MIN;
		this.A_MAX = A_MAX;
		this.A_MIN = A_MIN;

		this.X = new double[N][D];
		this.V = new double[N][D];
		this.Q = new double[N][1];
		this.F = new double[N];
		this.R = (R_MAX + R_MIN) / 2;
		this.A = (A_MIN + A_MAX) / 2;

		// Initialize bounds
		this.lb = new double[1][D];
		for ( int i = 0; i < D; i++ ){
			this.lb[0][i] = -2.0;
		}
		this.ub = new double[1][D];
		for ( int i = 0; i < D; i++ ){
			this.ub[0][i] = 2.0;
		}

		// Initialize Q and V
		for ( int i = 0; i < N; i++ ){
			this.Q[i][0] = 0.0;
		}
		for ( int i = 0; i < N; i++ ){
			for ( int j = 0; j < D; j++ ) {
				this.V[i][j] = 0.0;
			}
		}

		// Initialize X
		for ( int i = 0; i < N; i++ ){
			for ( int j = 0; j < D; j++ ){
				this.X[i][j] = lb[0][j] + (ub[0][j] - lb[0][j]) * rand.nextDouble();
			}
			this.F[i] = objective(X[i]);
		}

		// Find initial best solution
		int fmin_i = 0;
		for ( int i = 0; i < N; i++ ){
			if ( F[i] < F[fmin_i] )
				fmin_i = i;
		}

		// Store minimum fitness and it's index.
		// B holds the best solution array[1xD]
		this.fmin = F[fmin_i];
		this.B = X[fmin_i]; // (1xD)
	}

	private double objective(double[] Xi){
		double sum = 0.0;
		for ( int i = 0; i < Xi.length; i++ ){
			sum = sum + Xi[i] * Xi[i];
		}
		return sum;
	}

	private double[] simpleBounds(double[] Xi){
		// Don't know if this should be implemented
		return Xi;
	}

	private void startBat(){

		double[][] S = new double[N][D];
		int n_iter = 0;

		// Loop for all iterations/generations(MAX)
		for ( int t = 0; t < MAX; t++ ){
			// Loop for all bats(N)
			for ( int i = 0; i < N; i++ ){
				
				// Update frequency (Nx1)
				Q[i][0] = Q_MIN + (Q_MIN-Q_MAX) * rand.nextDouble();
				// Update velocity (NxD)
				for ( int j = 0; j < D; j++ ){
					V[i][j] = V[i][j] + (X[i][j] - B[j]) * Q[i][0];
				}
				// Update S = X + V
				for ( int j = 0; j < D; j++ ){
					S[i][j] = X[i][j] + V[i][j];
				}
				// Apply bounds/limits
				X[i] = simpleBounds(X[i]);
				// Pulse rate
				if ( rand.nextDouble() > R )
					for ( int j = 0; j < D; j++ )
						X[i][j] = B[j] + 0.001 * rand.nextGaussian();


				// Evaluate new solutions
				double fnew = objective(X[i]);

				// Update if the solution improves or not too loud
				if ( fnew <= F[i] && rand.nextDouble() < A ){
					X[i] = S[i];
					F[i] = fnew;
				}

				// Update the current best solution
				if ( fnew <= fmin ){
					B = X[i];
					fmin = fnew;
				}
			} // end loop for N
			n_iter = n_iter + N;
		} // end loop for MAX

		System.out.println("Number of evaluations : " + n_iter );
		System.out.println("Best = " + Arrays.toString(B) );
		System.out.println("fmin = " + fmin );
	}

	public static void main(String[] args) {
		new BA(20, 1000, 0.0, 1.0, 0.0, 1.0).startBat();
	}
}