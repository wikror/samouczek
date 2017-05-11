/*
 * Created on 1 gru 2014 ( Time 21:45:44 )
 * Generated by Telosys Tools Generator ( version 2.1.0 )
 */
// This Bean has a basic Primary Key (not composite) 

package pl.pwl.samouczek.orm.jpa;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
//import javax.validation.constraints.* ;
//import org.hibernate.validator.constraints.* ;

/**
 * Persistent class for entity stored in table "material"
 *
 * @author Telosys Tools Generator
 *
 */

@Entity
@Table(name="material")
// Define named queries here
@NamedQueries ( {
  @NamedQuery ( name="MaterialEntity.countAll", query="SELECT COUNT(x) FROM MaterialEntity x" )
} )
public class MaterialEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    //----------------------------------------------------------------------
    // ENTITY PRIMARY KEY ( BASED ON A SINGLE FIELD )
    //----------------------------------------------------------------------
    @Id
    @Column(name="ID", nullable=false)
    private Integer    id           ;


    //----------------------------------------------------------------------
    // ENTITY DATA FIELDS 
    //----------------------------------------------------------------------    
    @Column(name="title", nullable=false, length=255)
    private String     title        ;

    @Column(name="type", nullable=false, length=45)
    private String     type         ;

    @Column(name="contents")
    private String     contents     ;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="next", referencedColumnName="id")
    private MaterialEntity next         ;

	// "lessonid" (column "lessonid") is not defined by itself because used as FK in a link 


    //----------------------------------------------------------------------
    // ENTITY LINKS ( RELATIONSHIP )
    //----------------------------------------------------------------------
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="lessonid", referencedColumnName="id")
    private LessonEntity lesson;
    
    @ManyToMany(mappedBy="materials", fetch=FetchType.LAZY)
    private Set<UserEntity> users;


    //----------------------------------------------------------------------
    // CONSTRUCTOR(S)
    //----------------------------------------------------------------------
    public MaterialEntity() {
		super();
    }
    
    //----------------------------------------------------------------------
    // GETTER & SETTER FOR THE KEY FIELD
    //----------------------------------------------------------------------
    public void setId( Integer id ) {
        this.id = id ;
    }
    public Integer getId() {
        return this.id;
    }

    //----------------------------------------------------------------------
    // GETTERS & SETTERS FOR FIELDS
    //----------------------------------------------------------------------
    //--- DATABASE MAPPING : title ( VARCHAR ) 
    public void setTitle( String title ) {
        this.title = title;
    }
    public String getTitle() {
        return this.title;
    }

    //--- DATABASE MAPPING : type ( VARCHAR ) 
    public void setType( String type ) {
        this.type = type;
    }
    public String getType() {
        return this.type;
    }

    //--- DATABASE MAPPING : contents ( TEXT ) 
    public void setContents( String contents ) {
        this.contents = contents;
    }
    public String getContents() {
        return this.contents;
    }

    //--- DATABASE MAPPING : next ( INT ) 
    public void setNext( MaterialEntity next ) {
        this.next = next;
    }
    public MaterialEntity getNext() {
        return this.next;
    }


    //----------------------------------------------------------------------
    // GETTERS & SETTERS FOR LINKS
    //----------------------------------------------------------------------
    public void setLesson( LessonEntity lesson ) {
        this.lesson = lesson;
    }
    public LessonEntity getLesson() {
        return this.lesson;
    }


    //----------------------------------------------------------------------
    // toString METHOD
    //----------------------------------------------------------------------
    public String toString() { 
        StringBuffer sb = new StringBuffer(); 
        sb.append("["); 
        sb.append(id);
        sb.append("]:"); 
        sb.append(title);
        sb.append("|");
        sb.append(type);
        // attribute 'contents' not usable (type = String Long Text)
        sb.append("|");
        sb.append(next);
        return sb.toString(); 
    }

	public Set<UserEntity> getUsers() {
		return users;
	}

	public void setUsers(Set<UserEntity> users) {
		this.users = users;
	} 
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MaterialEntity) {
    		if (((MaterialEntity) obj).getId() != null && getId() != null) {
    			return getId().equals(((MaterialEntity) obj).getId());
    		}
    	}
    	return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		if (getId() != null) {
    		return getId().hashCode() * 2;
    	} else {
    		return (hashCode() * 2) + 1;
    	}
	}

}
