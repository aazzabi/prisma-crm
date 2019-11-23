package Enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RepairStatus {
	Completed, OnHold, Replaced, InProgress, Rejected
}