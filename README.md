### 스프링부트를 활용한 로그인 구현(10/20 ~ 11/20)

---
__○ 프로젝트 구조__

<div class="test_image">
  <img src="./img/archi.jpg">
</div>


__☆사용된 프로그램__
- InteliJ -- Java11
- H2 database

H2데이터 베이스에 데이터를 추가하고 불러오는 기능이 구현된 프로젝트입니다. 스프링 시큐리티와 RestFul API를 구현하려 했으나 잘 안됬습니다.
아무튼 회원가입과 로그인이 되긴합니다.




#### 구현 화면
---
<div class="test_image">
  <img src="./img/1.jpg">
</div>
<br>1. 초기화면
<br><div class="test_image">
  <img src="./img/2.jpg">
</div>
2. 일반 회원가입

<br><div class="test_image">
  <img src="./img/3.jpg">
</div>
3. 회원가입 성공

<br><div class="test_image">
  <img src="./img/4.jpg">
</div>
4. 로그인 화면

<br><div class="test_image">
  <img src="./img/5.jpg">
</div>
5. 메인 화면

<br><div class="test_image">
  <img src="./img/6.jpg">
</div>
6. 유저 정보 조회 및 삭제

<br><div class="test_image">
  <img src="./img/7.jpg">
</div>


### __※ 하자가 있는 사항__

- 로그인시 헤더에 토큰을 첨가하는 기능이 구현되지 않았습니다.
  HTML 파일에 직접 첨부해줘야 합니다ㅋ('Authorization': '~~')
