package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Shariar (Shawn) Emami
 */
@Entity
@Table(name = "image", catalog = "redditreader", schema = "")
@NamedQueries({
    @NamedQuery(name = "Image.findAll", query = "SELECT i FROM Image i join fetch i.board"),
    @NamedQuery(name = "Image.findById", query = "SELECT i FROM Image i join fetch i.board WHERE i.id = :id"),
    @NamedQuery(name = "Image.findByTitle", query = "SELECT i FROM Image i join fetch i.board WHERE i.title = :title"),
    @NamedQuery(name = "Image.findByUrl", query = "SELECT i FROM Image i join fetch i.board WHERE i.url = :url"),
    @NamedQuery(name = "Image.findByLocalPath", query = "SELECT i FROM Image i join fetch i.board WHERE i.localPath = :localPath"),
    @NamedQuery(name = "Image.findByBoardId", query = "SELECT i FROM Image i join fetch i.board WHERE i.board = :boardid"),
    @NamedQuery(name = "Image.findByDate", query = "SELECT i FROM Image i join fetch i.board WHERE i.date = :date")})
//by using fetch we grab the needed dependency when getting a new object.
public class Image implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1000)
    @Column(name = "title")
    private String title;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "url")
    private String url;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "local_path")
    private String localPath;
    @Basic(optional = false)
    @NotNull
    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @JoinColumn(name = "Board_id", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    //lazy fetch is used in entities with many to one relationships to prevent
    //hibernate from getting unneeded data till it is actually needed.
    //on the other hand eager will grab everything from beginning.
    private Board board;

    public Image() {
    }

    public Image(Integer id) {
        this.id = id;
    }

    public Image(Integer id, String title, String url, String localPath, Date date) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.localPath = localPath;
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Image)) {
            return false;
        }
        Image other = (Image) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Image[ id=" + id + " ]";
    }
    
}
