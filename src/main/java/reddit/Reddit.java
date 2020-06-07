package reddit;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;

/**
 * a wrapper class for jraw library to access Reddit API. visit this site for
 * more info about jraw: https://mattbdean.gitbooks.io/jraw/
 *
 * @author Shariar (Shawn) Emami
 */
public class Reddit {

    /**
     * you need to initialize the next three variables based on the given
     * instructions in prelab
     */
    private static final String CLIENT_ID = "";
    private static final String CLIENT_SECRET = "";
    private static final String REDDIT_USER = "";

    /**
     * replace yourname with your name in the text below, no spaces and
     * lowercase
     */
    private static final String APPID = "com.algonquin.cst8288.s20.yourname";

    private static final String PLATFORM = "RedditReaderWebBot";
    private static final String VERSION = "v0.1";

    private RedditClient reddit;
    private DefaultPaginator<Submission> paginator;
    private List<Listing<Submission>> pages;
    private Listing<Submission> currentPage;

    private void hasAuthenticated() {
        if (reddit == null) {
            throw new IllegalStateException("authenticate() Method in "
                    + "Scrapper must be called ONCE before everything "
                    + "regarding Scraper");
        }
    }

    private void hasPagesBeenConfiged() {
        if (paginator == null) {
            throw new IllegalStateException("buildRedditPagesConfig() Method "
                    + "in Scrapper must be called ONCE before requestNumberOfPages()");
        }
    }

    private void hasPagesBeenRequested() {
        if (pages == null) {
            throw new IllegalStateException("requestNumberOfPages() Method "
                    + "in Scrapper must be called ONCE before proccessPageNumber()");
        }
    }

    private void hasNextPageRequested() {
        if (currentPage == null) {
            throw new IllegalStateException("requestNextPage() Method "
                    + "in Scrapper must be called ONCE before proccessNextPage()");
        }
    }

    /**
     * get permission from Reddit to access their API
     *
     * @return current object of Scraper, this
     */
    public Reddit authenticate() {
        UserAgent userAgent = new UserAgent(PLATFORM, APPID, VERSION, REDDIT_USER);
        Credentials credentials = Credentials.userless(CLIENT_ID, CLIENT_SECRET, UUID.randomUUID());
        NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);
        reddit = OAuthHelper.automatic(adapter, credentials);
        return this;
    }

    /**
     * configure what subreddit to be downloaded
     *
     * @param subreddit    - name of subreddit to access
     * @param postsPerPage - number of post per page to download, max 10
     * @param sort         - in what order to sort the posts, ex. Sort.HOT
     *
     * @return current object of Scraper, this
     */
    public Reddit buildRedditPagesConfig(String subreddit, int postsPerPage, Sort sort) {
        hasAuthenticated();

        paginator = reddit
                .subreddit(subreddit)
                .posts()
                .limit(postsPerPage)
                .sorting(sort.value())
                .build();
        return this;
    }

    /**
     * get the next Reddit page. first page if next hasn't been called yet.
     *
     * @return current object of Scraper, this
     */
    public Reddit requestNextPage() {
        hasAuthenticated();
        hasPagesBeenConfiged();

        currentPage = paginator.next();
        return this;
    }

    /**
     * request number of pages to be downloaded. do not go for a high number,
     * max 5
     *
     * @deprecated use {@link Reddit#requestNextPage()} instead
     *
     * @param totalPages - number of pages to download, max 5
     *
     * @return current object of Scraper, this
     */
    @Deprecated
    public Reddit requestNumberOfPages(int totalPages) {
        hasAuthenticated();
        hasPagesBeenConfiged();

        pages = paginator.accumulate(totalPages);
        return this;
    }

    /**
     * start processing each page using the callback lambda. callback lambda
     * uses the Post class to access data in each post. this lambda will be
     * called for every single post one at a time.
     *
     * @param callback - callback lambda of type Post class
     *
     * @return current object of Scraper, this
     */
    public Reddit proccessNextPage(Consumer<Post> callback) {
        hasAuthenticated();
        hasPagesBeenConfiged();
        hasNextPageRequested();

        currentPage.forEach(submission -> {
            callback.accept(new Post(submission));
        });
        return this;
    }

    /**
     * start processing each page using the callback lambda. callback lambda
     * uses the Post class to access data in each post. this lambda will be
     * called for every single post one at a time.
     *
     * @deprecated use {@link Reddit#proccessNextPage(java.util.function.Consumer)} instead
     *
     * @param pageNumber - page number to process, -1 for all
     * @param callback   - callback lambda of type Post class
     *
     * @return current object of Scraper, this
     */
    @Deprecated
    public Reddit proccessPageNumber(int pageNumber, Consumer<Post> callback) {
        hasAuthenticated();
        hasPagesBeenConfiged();
        hasPagesBeenRequested();

        int start = pageNumber == -1 ? 0 : pageNumber;
        int end = pageNumber == -1 ? pages.size() : pageNumber + 1;

        for (int i = start; i < end; i++) {
            Listing<Submission> firstPage = pages.get(pageNumber);
            firstPage.forEach(submission -> {
                callback.accept(new Post(submission));
            });
        }
        return this;
    }
}
