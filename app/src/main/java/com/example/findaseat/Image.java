package com.example.findaseat;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

public class Image extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = findViewById(R.id.imageView);

        // Replace "YOUR_IMAGE_URL" with the actual URL of the image you want to display
        String imageUrl = "https://en.wikipedia.org/wiki/Cat#/media/File:Siam_lilacpoint.jpg";

        // Use Picasso to load the image asynchronously
        Picasso.get().load(imageUrl).into(imageView);
    }
}
