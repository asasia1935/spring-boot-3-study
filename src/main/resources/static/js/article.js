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

// 수정 기능
const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
    modifyButton.addEventListener('click', event => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        fetch(`/api/articles/${id}`, {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                title: document.getElementById('title').value,
                content: document.getElementById('content').value
            })
        })
            .then(() => {
                alert('수정이 완료되었습니다.');
                location.replace(`/articles/${id}`);
            });
    });
}

// 생성 기능
const createButton = document.getElementById('create-btn');

if (createButton) {
    createButton.addEventListener('click', event => {
        fetch('/api/articles', {
            method: 'POST',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                title: document.getElementById('title').value,
                content: document.getElementById('content').value
            })
        })
            .then(() => {
                alert('등록 완료되었습니다.');
                location.replace('/articles');
            });
    });
}