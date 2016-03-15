CarouselView
=======

A simple yet flexible library to add carousel view in your android application.

Download
--------
####Gradle:
```groovy
compile 'com.synnapps:carouselview:0.0.1'
```
####Maven:
```xml
<dependency>
  <groupId>com.synnapps</groupId>
  <artifactId>carouselview</artifactId>
  <version>0.0.1</version>
  <type>pom</type>
</dependency>
```

Usage
--------

####Include following code in your layout:

```xml
    <com.synnapps.carouselview.CarouselView
        android:id="@+id/carouselView"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent"
        carousel:slideInterval="5000">
    </com.synnapps.carouselview.CarouselView>
```
####Include following code in your activity:
```java
public class SampleCarouselViewActivity extends AppCompatActivity {

    CarouselView carouselView;

    int[] sampleImages = {R.drawable.image_1, R.drawable.image_2, R.drawable.image_3, R.drawable.image_4, R.drawable.image_5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_carousel_view);

        carouselView = (CarouselView) findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);

        carouselView.setImageListener(imageListener);
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };

}
```

Developed By
--------
- Sayyam Mehmood
- Muhammad Rehan

Special Thanks
--------

This library uses code snippet from Jake Wharton's [ViewPagerIndicator][1] to display page indicator.

License
--------

    Copyright 2013 Square, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    
    [1]: https://github.com/JakeWharton/ViewPagerIndicator





