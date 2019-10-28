package Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity

public class Quotation {
@Id
@GeneratedValue(strategy=GenerationType.TABLE)
private int id;
}
