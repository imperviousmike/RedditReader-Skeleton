package reddit;

import common.FileUtility;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * this is just a test class to see if your Reddit credentials are correct.
 *
 * @author Shariar (Shawn) Emami
 */
public class TestRunReddit {

    /**
     * old style do not use.
     *
     * @Deprecated this is an outdated example look at {@link Reddit#exampleForReadingNextPage()}
     * @throws IOException
     */
    @Deprecated
    private static void exampleForReadingManyPages() throws IOException {
        FileUtility.createDirectory("img/");

        //example of how to download the first 30 hot images from Wallpaper subreddit
        Reddit scrap = new Reddit();
        scrap.authenticate()
                .buildRedditPagesConfig("Wallpaper", 30, Sort.HOT)
                .requestNumberOfPages(1)
                .proccessPageNumber(0, (Post post) -> {
                    if (post.isImage() && !post.isOver18()) {
                        String path = post.getUrl();
                        FileUtility.downloadAndSaveFile(path, "img");
                    }
                });
    }

    /**
     * use this example for your code.
     *
     * @throws IOException
     */
    private static void exampleForReadingNextPage() throws IOException {
        //create a directory in your project called img
        FileUtility.createDirectory("img/");

        //create a lambda that accepts post
        Consumer<Post> saveImage = (Post post) -> {
            //if post is an image and SFW
            if (post.isImage() && !post.isOver18()) {
                //get the path for the image which is unique
                String path = post.getUrl();
                //save it in img directory
                FileUtility.downloadAndSaveFile(path, "img");
            }
        };

        //create a new scraper
        Reddit scrap = new Reddit();
        //authenticate and set up a page for wallpaper subreddit with 5 posts soreted by HOT order
        scrap.authenticate().buildRedditPagesConfig("Wallpaper", 5, Sort.BEST);
        //get the next page 3 times and save the images.
        scrap.requestNextPage().proccessNextPage(saveImage);
    }

    /**
     * only run this for testing the scraper it will not run the project.
     *
     * @param args
     *
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        exampleForReadingNextPage();
    }
}
