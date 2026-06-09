#!/usr/bin/env python3

import requests
import sys
import os


def update_modrinth_desc(id, description):
    token = os.environ["MODRINTH_API_TOKEN"]

    headers = {"Content-Type": "application/json", "Authorization": f"Bearer {token}"}

    data = {"body": description}

    url = f"https://api.modrinth.com/v2/project/{id}"
    response = requests.patch(url, headers=headers, json=data)

    if response.status_code == 204:
        print("✅ modrinth description updated successfully")
        return 0
    else:
        print(f"Failed to update description. Status code: {response.status_code}")
        body = response.json()
        print(body["error"])
        print(body["description"])
        return response.status_code


def main():
    project_id = os.environ["MODRINTH_PROJECT_ID"]
    print(f"project id: {project_id}")

    if not project_id:
        return -1

    with open("README.md", "r", encoding="utf-8") as f:
        content = f.read()
        return update_modrinth_desc(project_id, content)


if __name__ == "__main__":
    sys.exit(main())
