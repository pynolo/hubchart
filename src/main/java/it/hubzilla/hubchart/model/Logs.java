package it.hubzilla.hubchart.model;

import it.hubzilla.hubchart.AppConstants;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author paolo
 */
@Entity
@Table(name = "logs")
public class Logs extends BaseEntity {
	private static final long serialVersionUID = 4809756595556670571L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id", nullable = false)
	private Integer id;
	@Column(name = "level", length = 16)
	private String level;
	@Basic(optional = false)
	@Column(name = "time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date time;
	@Basic(optional = false)
	@Column(name = "message", nullable=false, length = 65000)
	private String message;

	public String getFormattedTime() {
		String result = "";
		if (time != null) result = AppConstants.FORMAT_TIMESTAMP.format(time);
		return result;
	}
	
	public Logs() {
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Logs)) {
			return false;
		}
		Logs other = (Logs) object;
		if ((this.id == null && other.id != null)
				|| (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Logs[id=" + id + "]";
	}

}
