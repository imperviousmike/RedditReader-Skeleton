package entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Shariar (Shawn) Emami
 */
@Entity
@Table(name = "board", catalog = "redditreader", schema = "")
@NamedQueries({
    @NamedQuery(name = "Board.findAll", query = "SELECT b FROM Board b join fetch b.hostid"),
    @NamedQuery(name = "Board.findById", query = "SELECT b FROM Board b join fetch b.hostid WHERE b.id = :id"),
    @NamedQuery(name = "Board.findByUrl", query = "SELECT b FROM Board b join fetch b.hostid WHERE b.url = :url"),
    @NamedQuery(name = "Board.findByHostId", query = "SELECT b FROM Board b join fetch b.hostid WHERE b.hostid = :hostid"),
    @NamedQuery(name = "Board.findByName", query = "SELECT b FROM Board b join fetch b.hostid WHERE b.name = :name")})
public class Board implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "url")
    private String url;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "board", fetch = FetchType.LAZY)
    private List<Image> imageList;
    @JoinColumn(name = "Host_id", referencedColumnName = "id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Host hostid;

    public Board() {
    }

    public Board(Integer id) {
        this.id = id;
    }

    public Board(Integer id, String url, String name) {
        this.id = id;
        this.url = url;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Image> getImageList() {
        return imageList;
    }

    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
    }

    public Host getHostid() {
        return hostid;
    }

    public void setHostid(Host hostid) {
        this.hostid = hostid;
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
        if (!(object instanceof Board)) {
            return false;
        }
        Board other = (Board) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Board[ id=" + id + " ]";
    }
    
}
