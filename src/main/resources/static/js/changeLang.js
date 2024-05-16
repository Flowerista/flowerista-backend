document.addEventListener('DOMContentLoaded', function () {
    const langLinks = document.querySelectorAll('.dropdown-item');

    langLinks.forEach(link => {
        link.addEventListener('click', function () {
            const lang = this.getAttribute('data-lang');
            setLanguage(lang);
        });
    });

    const lang = getLanguageFromCookie();
    if (lang) {
        addLangParamToAllLinks(lang); // Обновляет все ссылки при загрузке страницы
        setLangInputValue(lang);
    }
});

function setLangInputValue(lang) {
    const langInput = document.getElementById('languageInput');
    if (langInput) {
        langInput.value = lang;
    } else {
        console.error('Language input field not found');
    }
}

function setLanguage(lang) {
    document.cookie = "lang=" + lang + "; path=/; max-age=" + 60 * 60 * 24 * 30;
    window.location.search = '?lang=' + lang; // Это обновит страницу, поэтому нет необходимости вызывать addLangParamToAllLinks здесь
}

function getLanguageFromCookie() {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; lang=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
    return null;
}

function addLangParamToAllLinks(lang) {
    const links = document.querySelectorAll('a');
    links.forEach(link => {
        let originalUrl = link.href;
        if (originalUrl) {
            let newUrl = new URL(originalUrl, window.location.origin);
            newUrl.searchParams.set('lang', lang); // Добавляет или обновляет параметр 'lang'
            link.href = newUrl.href;
        }
    });
}
