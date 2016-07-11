package clindo.contextJ.demo;

import static clindo.contextJ.file.ContextJ.*;

public class Employer implements IEmployer {

	private String name;
	private String address;

	public Employer(String newName, String newAddress) {
		this.name = newName;
		this.address = newAddress;
	}

	public String toString() {
		return layers.select().toString();
	}

	private LayerDefinitions<IEmployer> layers = new LayerDefinitions<IEmployer>(
			new IEmployer() {
				public String toString() {
					return "Name: " + name;
				}
			});

	{
		layers.define(Layers.Address, new IEmployer() {
			public String toString() {
				return layers.next(this) + "; Address: " + address;
			}
		});
	}
}
