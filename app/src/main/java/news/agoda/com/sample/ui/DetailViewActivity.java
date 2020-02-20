package news.agoda.com.sample.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.request.ImageRequest;

import news.agoda.com.sample.R;

/**
 * News detail view
 */
public class DetailViewActivity extends Activity {
    public static final String STORY_URL = "storyURL";
    public static final String TITLE = "title";
    public static final String SUMMARY = "summary";
    public static final String IMAGE_URL = "imageURL";
    private String storyURL = "";
    private String imageURL = "";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            storyURL = extras.getString(STORY_URL);
            String title = extras.getString(TITLE);
            String summary = extras.getString(SUMMARY);
            if (extras.containsKey(IMAGE_URL))
                imageURL = extras.getString(IMAGE_URL);

            TextView titleView = (TextView) findViewById(R.id.title);
            DraweeView imageView = (DraweeView) findViewById(R.id.news_image);
            TextView summaryView = (TextView) findViewById(R.id.summary_content);

            titleView.setText(title);
            summaryView.setText(summary);

            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(ImageRequest.fromUri(Uri.parse(imageURL)))
                    .setOldController(imageView.getController()).build();
            imageView.setController(draweeController);

        }
    }

    public void onFullStoryClicked(View view) {
        // #TODO Please, check on connection
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(storyURL));
        startActivity(intent);
    }
}
