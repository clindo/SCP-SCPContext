package clindo.contextJ.demo;

import static clindo.contextJ.file.ContextJ.*;

public class Demo {

	public static void main(String[] args) {

		final Employer tuD = new Employer("TU Darmstadt", "Darmstadt, Germany");
		final Person somePerson = new Person("Clindo Devassy K",
				"Kerala, India", tuD);

		System.out.println("The following output is context independent.");
		System.out.println(somePerson);
		System.out
				.println("---------------------------------------------------------------");

		System.out.println("The following output includes the address layer.");
		
		// The address of the Employer is not printed as the context of employment is not present.
		with(Layers.Address).eval(new Block() {
			public void eval() {
				System.out.println(somePerson);
			}
		});
		System.out
				.println("---------------------------------------------------------------");

		System.out
				.println("The following output includes the address and the employment layers.");
		with(Layers.Address, Layers.Employment).eval(new Block() {
			public void eval() {
				System.out.println(somePerson);
			}
		});
		System.out.println("---------------------------------------------------------------");

		System.out
				.println("Depending on whether arguments were passed to this program, the following output may or may not include the employment layer.");
		with(
				args.length > 0 ? new Layer[] { Layers.Employment }
						: new Layer[] {}).eval(new Block() {
			public void eval() {
				System.out.println(somePerson);			}
		});
		System.out.println();
	}
}
