import Endpoint from './Endpoint.js';

const endpoint = new Endpoint('/index');
let objectType = null;

window.renderjson.set_show_to_level(2);
function setOutput(response) {
    document.querySelector("#json").replaceChildren(window.renderjson(response));
}

async function refreshIndices() {
    const selected = document.querySelector("#indices").value;

    const res = await endpoint.get();
    let html = "";
    for(let index of res.indices) {
        html += `<option value=${index}>${index}</option>`
    }

    objectType = selected ? selected : res.indices[0];
    document.querySelector('#indices').innerHTML = html;
    document.querySelector(`#indices option[value="${objectType}"]`).selected = true;
}

window.addEventListener('DOMContentLoaded', refreshIndices);

document.querySelector("#indices").onchange = function (e) {
    objectType = document.querySelector("#indices").value;
}

//document.querySelector("#submit").onclick = function (e) {
//    const data = document.querySelector("#data").value;
//    endpoints[objectType].post("", data);
//}


window.execute = {
    list: async function(path) {
        const res = await endpoint.get(path);
        setOutput(res);
    },
    get: async function() {
        const res = await endpoint.get(objectType);
        setOutput(res);
    },
    create: async function() {
        const data = document.querySelector("#data").value;
        const newIndexName = document.querySelector("#index-name").value;
        if (!data) {
            alert("You have to provide input data");
            return;
        }

        const res = await endpoint.post(newIndexName ? newIndexName : objectType, JSON.parse(data));
        if (newIndexName) refreshIndices();
        document.querySelector("#index-name").value = "";
        setOutput(res);
    },
    remove: async function() {
        const res = await endpoint.remove(objectType);
        setOutput(res);
        refreshIndices();
    },
    post: async function() {
        const data = document.querySelector("#data").value;

        if (!data) {
            alert("You have to provide input data");
            return;
        }

        const res = await endpoint.post(objectType, data);
        setOutput(res);

    },
    update: async function() {
        const data = document.querySelector("#data").value;
        const json = JSON.parse(data);
        const res = await endpoint.put(objectType, json.docId, JSON.parse(data));
        setOutput(res);
    }
};


