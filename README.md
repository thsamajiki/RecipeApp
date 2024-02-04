# SeoultechRecipe
요리 레시피 공유 앱

## 개요
나의 요리 레시피를 다른 사람들과 공유함으로써 요리를 할 때 많은 도움을 받을 수 있는 앱입니다.
    

## 프로젝트 기간
2020\. 03 ~ 06


## 기여도
- 기획, 개발, 디자인 모두 했습니다.


## 사용 프로그램 및 언어
- 사용 프로그램 : Android Studio, Google Firebase, GitHub
- 사용 언어 : Java


## 앱의 버전
- minSdkVersion : 21
- targetSdkVersion : 34


## 이용 대상
- 기숙사 생활/자취를 하는 학생들
- 1인 가구


## 주요 기능
- 학생들은 앱에 자신만의 요리 레시피를 피드에 업로드하여 공유할 수 있다.

- 채팅을 통해 레시피 관련 대화를 할 수 있다.

- 피드에 있는 레시피에 대해 평가를 할 수 있다.


## 사용된 기술
- MVC 패턴
- SharedPreferences
- ViewPager2
- RecyclerView
- Response 클래스 구현


## 사용된 라이브러리
- Material 디자인
- 포토뷰 (com.github.chrisbanes:PhotoView)
- 사진 첨부 (Glide)
- 이미지 피커 (com.github.esafirm.android-image-picker:imagepicker)



## 개발 후 느낀 점
- 요리 사진을 업로드할 수 있도록 구현했지만, 사진을 1장 밖에 올리지 못하는 것은 아쉬운 점입니다.<br>
요리 결과물만 사진으로 올릴 것이 아니라, 레시피 사진들도 직접 업로드할 수 있도록 기능을 구현하면 좋겠다는 생각이 들었습니다.
- 채팅 기능을 성공적으로 구현해서 뿌듯했습니다.
- 기존에 출시된 요리 레시피 공유 앱들보다 좀 더 차별화되는 기능을 추가하면 좋을 것이라 생각하여 현재 고민중입니다.<br><br>

<strong>코틀린으로 리팩토링했으며(<a href="https://github.com/thsamajiki/RecipeSpace">RecipeSpace</a>), MVVM 패턴과 클린 아키텍처를 적용했습니다.</strong>


## 스크린샷
<img src="/images/Splash.png" width="180px" height="320px" title="Splash" alt="Splash"></img>
<img src="/images/Login.png" width="180px" height="320px" title="Login" alt="Login"></img>
<img src="/images/SignUp.png" width="180px" height="320px" title="SignUp" alt="SignUp"></img>
<img src="/images/RecipeFeed.png" width="180px" height="320px" title="RecipeFeed" alt="RecipeFeed"></img>
<img src="/images/FeedDetail.png" width="180px" height="320px" title="FeedDetail" alt="FeedDetail"></img>
<img src="/images/Upload.png" width="180px" height="320px" title="Upload" alt="Upload"></img>
<img src="/images/ChatList.png" width="180px" height="320px" title="ChatList" alt="ChatList"></img>
<img src="/images/ChattingRoom.png" width="180px" height="320px" title="ChattingRoom" alt="ChattingRoom"></img>
<img src="/images/Profile.png" width="180px" height="320px" title="Profile" alt="Profile"></img>
<img src="/images/ProfileEdit.png" width="180px" height="320px" title="ProfileEdit" alt="ProfileEdit"></img>


## 시연 영상
<img src="/images/레시피앱 시연.gif" width="360px" height="640px" title="test_video" alt="Test_video"></img>
