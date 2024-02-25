// 삭제 기능
// id를 delete-btn으로 설정한 엘리먼트를 찾음
const deleteButton = document.getElementById('delete-btn');

if(deleteButton) {
    deleteButton.addEventListener('click', event => {
        // 클릭 이벤트가 발생하면 fetch 메서드를 통해 요청을 보냄
        let id = document.getElementById('article-id').value;
        fetch(`/api/articles/${id}`, {
            method: 'DELETE'
        }) // fetch가 잘 되면 연이어 실행되는 메서드
            .then(() => {
                alert('삭제가 완료되었습니다.');
                location.replace('/articles');
            });
    });
}