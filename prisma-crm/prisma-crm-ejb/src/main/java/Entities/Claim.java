package Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import Enums.*;

@Entity
public class Claim implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int id;

	@Column(nullable=true)
	private String title;

	@Column
	private String code;

	@Column(nullable=false)
	private String description;

	@Enumerated(EnumType.STRING)
	private ClaimPriority priority;

	@Enumerated(EnumType.STRING)
	private ClaimStatus status;

	@Enumerated(EnumType.STRING)
	private ClaimType type;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date createdAt;
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date openedAt;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date delegatedAt;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date resolvedAt;

	@ManyToOne
	private User createdBy;
	
	private int createdById;

	@OneToOne
	private Agent resolvedBy;//---Ok---

	@ManyToOne
	private Agent firstResponsable;

	@ManyToOne
	private Agent responsable;//---Ok---
/*
	//@JsonIgnore
	@OneToMany(mappedBy = "claim", cascade=CascadeType.REMOVE, fetch= FetchType.LAZY)
	private List<NoteClaim> notes;//---Ok---
*/
	private Boolean isFaq;
	
	public Claim() {
		this.status = ClaimStatus.EN_ATTENTE;
		this.createdAt = new Date(System.currentTimeMillis());
//		this.notes = new ArrayList<NoteClaim>();
		this.isFaq = false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ClaimPriority getPriority() {
		return priority;
	}

	public void setPriority(ClaimPriority priority) {
		this.priority = priority;
	}

	public ClaimStatus getStatus() {
		return status;
	}

	public void setStatus(ClaimStatus status) {
		this.status = status;
	}

	public ClaimType getType() {
		return type;
	}

	public void setType(ClaimType type) {
		this.type = type;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getOpenedAt() {
		return openedAt;
	}

	public void setOpenedAt(Date openedAt) {
		this.openedAt = openedAt;
	}

	public Date ResolvedA() {
		return resolvedAt;
	}

	public void setResolvedAt(Date resolvedAt) {
		this.resolvedAt = resolvedAt;
	}

	public Agent getResolvedBy() {
		return resolvedBy;
	}

	public void setResolvedBy(Agent resolvedBy) {
		this.resolvedBy = resolvedBy;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}
/*
	public List<NoteClaim> getNotes() {
		return notes;
	}

	public void setNotes(List<NoteClaim> notes) {
		this.notes = notes;
	}

	public void addNote(NoteClaim note) {
		note.setClaim(this);
		this.notes.add(note);
	}

	public void removeNote(NoteClaim note) {
		this.notes.remove(note.getId());
		note.setClaim(null);
	}
*/
	public Agent getResponsable() {
		return responsable;
	}

	public void setResponsable(Agent responsable) {
		this.responsable = responsable;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getResolvedAt() {
		return resolvedAt;
	}

	public Date getDelegatedAt() {
		return delegatedAt;
	}

	public void setDelegatedAt(Date delegatedAt) {
		this.delegatedAt = delegatedAt;
	}

	public Agent getFirstResponsable() {
		return firstResponsable;
	}

	public void setFirstResponsable(Agent firstResponsable) {
		this.firstResponsable = firstResponsable;
	}

	public Boolean getIsFaq() {
		return isFaq;
	}

	public void setIsFaq(Boolean isFaq) {
		this.isFaq = isFaq;
	}
	
	public int getCreatedById() {
		return createdById;
	}

	public void setCreatedById(int createdById) {
		this.createdById = createdById;
	}

	@Override
	public String toString() {
		return "Claim [id=" + id + ", title=" + title + ", code=" + code + ", description=" + description
				+ ", priority=" + priority + ", status=" + status + ", type=" + type + ", createdAt=" + createdAt
				+ ", openedAt=" + openedAt + ", delegatedAt=" + delegatedAt + ", resolvedAt=" + resolvedAt
				+ ", createdBy=" + createdBy + ", resolvedBy=" + resolvedBy + ", firstResponsable=" + firstResponsable
				+ ", responsable=" + responsable + ", isFaq"+ isFaq+"]";
	}

}
