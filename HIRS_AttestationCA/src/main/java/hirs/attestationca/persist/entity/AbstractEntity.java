package hirs.attestationca.persist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * An abstract database entity.
 */
@ToString
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

    /**
     * static value for the length of a status message for objects that
     * can have extremely long values, potentially.
     */
    protected static final int RESULT_MESSAGE_LENGTH = 1000000;

    @Id
    @Column(name = "id")
    @UuidGenerator(style = UuidGenerator.Style.AUTO)
    @GeneratedValue
    @Getter
    private UUID id;

    @Column(name = "create_time")
    private Date createTime;

    /**
     * Default empty constructor is required for Hibernate. It is protected to
     * prevent code from calling it directly.
     */
    protected AbstractEntity() {
        super();
        createTime = new Date();
    }

    /**
     * Setter for the UUID that can not be null
     * and can not be overridden.
     *
     * @param id - primary able key
     */
    public void setId(final UUID id) {
        if (id != null) {
            this.id = id;
        }
    }

    /**
     * Returns the creation time of this entity.
     *
     * @return creation time
     */
    public Date getCreateTime() {
        if (createTime == null) {
            createTime = new Date();
        }
        return (Date) createTime.clone();
    }

    /**
     * Reset the creation time to the current time.
     */
    public void resetCreateTime() {
        createTime.setTime(new Date().getTime());
    }

    /**
     * Generates an integer hash code for this entity.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return super.hashCode();
    }

    /**
     * Compares this entity to the provided object to verify
     * that both objects are equal.
     *
     * @param object object to compare
     * @return true if they are equal, false otherwise
     */
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (!(this.getClass().equals(object.getClass()))) {
            return false;
        }
        return this.hashCode() == object.hashCode();
    }

}
