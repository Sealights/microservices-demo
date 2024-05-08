import random
import requests
import pytest
import os
import threading


BASE_URL = os.environ.get("machine_dns")  # internal or external test
numbers=["1","4","5"]
def test_added():
    response = requests.get(BASE_URL + "/added")
    assert response.status_code == 200

def test_added_num():
    for num in numbers:
        response = requests.get(BASE_URL + "/added/number/"+num)
        print(response.text)
        assert response.status_code == 200
        Switcher={
            "1": "Number not equal 5 nor 4",
            "4": "Number equal 4Function in another file",
            "5": "Number equals 5Nested function"
        }
        assert response.json()['message'] == Switcher.get(num)
        

def test_added_content_get():
    response = requests.get(BASE_URL + "/added/content")
    assert response.status_code == 200
    assert response.text == "Method is GET"

def test_added_content_delete():
    response = requests.delete(BASE_URL + "/added/content")
    assert response.status_code == 200
    assert response.text == "Method is DELETE"

def test_added_content_post():
    Switcher={
        "1": "Nested function Content none of the ifs",
        "4": "Num is 4",
        "5": "Num is 5"
    }
    for num in numbers:
        data = {
            'num': int(num),
        }
        response = requests.post(BASE_URL + "/added/content",json=data)
        assert response.text == Switcher.get(num)
        assert response.status_code == 200
        



if __name__ == "__main__":
    pytest.main()
