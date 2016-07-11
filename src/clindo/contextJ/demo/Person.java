package clindo.contextJ.demo;

import static clindo.contextJ.file.ContextJ.*;

public class Person implements IPerson {

	private String name;
	private String address;
	private IEmployer employer;

	public Person(String newName, String newAddress, IEmployer newEmployer) {
		this.name = newName;
		this.address = newAddress;
		this.employer = newEmployer;
	}

	public String toString() {
		return layers.select().toString();
	}

	private LayerDefinitions<IPerson> layers = new LayerDefinitions<IPerson>(
			new IPerson() {
				public String toString() {
					return "Name: " + name;
				}
			});

	{
		layers.define(Layers.Address, new IPerson() {
			public String toString() {
				return layers.next(this) + "; Address: " + address;
			}
		});

		layers.define(Layers.Employment, new IPerson() {
			public String toString() {
				return layers.next(this) + "; [Employer] " + employer;
			}
		});
	}
}
