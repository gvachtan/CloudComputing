export default class Endpoint {
    url;

    constructor(url) {
        this.url = url;
    }

    async get(path = "") {
        const response = await fetch(`${this.url}/${path}`);
        const data = await response.json();
        console.log(data);
        return data;
    }

    async post(path = "", data) {
        const response = await fetch(`${this.url}/${path}`, {
            method: 'POST', // *GET, POST, PUT, DELETE, etc.
            mode: 'cors', // no-cors, *cors, same-origin
            cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
            credentials: 'same-origin', // include, *same-origin, omit
            headers: {
              'Content-Type': 'application/json'
              // 'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: data && JSON.stringify(data) // body data type must match "Content-Type" header
        });
        return response.json();
    }

    async put(path = "", docId,  data) {
        try {
            const response = await fetch(`${this.url}/${path}/${docId}`, {
              method: 'PUT',
              body: data && JSON.stringify(data)
            });

            const result = await response.json();
            console.log('Success:', result);
            return result;
        } catch (error) {
            console.error('Error:', error);
        }
    }

    async remove(path = "") {
        try {
            const response = await fetch(`${this.url}/${path}`, {
              method: 'DELETE',
            });

            const result = await response.json();
            console.log('Success:', result);
            return result;
        } catch (error) {
            console.error('Error:', error);
        }
    }
}
