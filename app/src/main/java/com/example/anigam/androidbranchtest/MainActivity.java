package com.example.anigam.androidbranchtest;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButtonListeners();
    }

    @Override
    public void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance();

        Branch.getInstance().initSession(new Branch.BranchUniversalReferralInitListener() {
            @Override
            public void onInitFinished(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties, BranchError branchError) {
                JSONObject incoming_data = new JSONObject();
                if (branchUniversalObject == null) {
                    //Do nothing
                } else {
                    incoming_data = branchUniversalObject.convertToJson();
                    TextView text = (TextView) findViewById(R.id.dataTextView);
                    text.setText(incoming_data.toString());
                }
            }

        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    protected void setIdentity(String id) {
        Branch.getInstance().setIdentity(id);
    }

    protected void setButtonListeners() {
        Button createLinkButton = (Button) findViewById(R.id.banchLinkbutton);
        createLinkButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                TextView text = (TextView) findViewById(R.id.textView);
                text.setText(" Creating Branch link here");
                createBranchLinks(false);
            }
        });

        Button shareSheetButton = (Button) findViewById(R.id.setIdentity);
        shareSheetButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                TextView textview = (TextView) findViewById(R.id.editText);
                String identity = textview.getText().toString();
                setIdentity(identity);
            }
        });
    }

    protected void createBranchLinks(boolean sharesheet) {
        BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier("item/12345")
                .setTitle("Sample Branch Link")
                .setContentDescription("Bird Images")
                .setContentImageUrl("http://imgsv.imaging.nikon.com/lineup/lens/zoom/normalzoom/af-s_dx_18-140mmf_35-56g_ed_vr/img/sample/sample1_l.jpg")
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .addContentMetadata("bird", "orange Bird");

        LinkProperties linkProperties = new LinkProperties()
                .setChannel("Facebook");
        if (sharesheet) {
            createShareSheet(branchUniversalObject, linkProperties);
        } else {
            DisplayLink(branchUniversalObject, linkProperties);
        }
    }

    protected void DisplayLink(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties) {
        branchUniversalObject.generateShortUrl(this, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error == null) {
                    TextView text = (TextView) findViewById(R.id.textView);
                    text.setText(url);
                }
            }
        });
    }

    protected void createShareSheet(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties) {

        ShareSheetStyle shareSheetStyle = new ShareSheetStyle(MainActivity.this, "Check this out!", "This stuff is awesome: ")
                .setCopyUrlStyle(getResources().getDrawable(android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
                .setMoreOptionStyle(getResources().getDrawable(android.R.drawable.ic_menu_search), "Show more")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                .setAsFullWidthStyle(true)
                .setSharingTitle("Share With");

        branchUniversalObject.showShareSheet(this,
                linkProperties,
                shareSheetStyle,
                new Branch.BranchLinkShareListener() {
                    @Override
                    public void onShareLinkDialogLaunched() {
                    }

                    @Override
                    public void onShareLinkDialogDismissed() {
                    }

                    @Override
                    public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {
                    }

                    @Override
                    public void onChannelSelected(String channelName) {
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
