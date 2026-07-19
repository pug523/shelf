#!/usr/bin/env python3

import os
import re
import sys
import requests


def rewrite_relative_paths(content: str, repo: str, branch: str) -> str:
    """
    Transforms relative asset paths (Markdown and HTML) into absolute GitHub raw URLs.
    Example: .github/assets/screen.png -> https://raw.githubusercontent.com/user/repo/main/.github/assets/screen.png
    """
    base_url = f"https://raw.githubusercontent.com/{repo}/{branch}"

    # Match HTML img tags: <img src=".github/assets/pic.png"> or <img ... src='.github/...'>
    # Captures paths that do not start with http://, https://, or /
    html_pattern = r'(<img\s+[^>]*src=["\'])(?!(?:https?://|/))([^"\']+)(["\'][^>]*>)'
    content = re.sub(html_pattern, rf"\1{base_url}/\2\3", content)

    # Match Markdown image/link syntax: ![Alt text](.github/assets/pic.png)
    md_pattern = r"(!\[[^\]]*\]\()(?!(?:https?://|/))([^\)]+)(\))"
    content = re.sub(md_pattern, rf"\1{base_url}/\2\3", content)

    return content


def update_modrinth_desc(project_id: str, description: str) -> int:
    token = os.environ.get("MODRINTH_API_TOKEN")
    if not token:
        print("❌ Error: MODRINTH_API_TOKEN environment variable is not set.")
        return 1

    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {token}",
        # Modrinth API guidelines highly recommend setting a unique User-Agent header
        "User-Agent": "GitHub-CI/Modrinth-Sync-Script (GitHub Actions)",
    }
    data = {"body": description}
    url = f"https://api.modrinth.com/v2/project/{project_id}"

    try:
        response = requests.patch(url, headers=headers, json=data, timeout=15)
    except requests.RequestException as e:
        print(f"❌ Network error while connecting to Modrinth: {e}")
        return 1

    if response.status_code == 204:
        print("✅ Modrinth description updated successfully!")
        return 0

    print(f"❌ Failed to update description. Status code: {response.status_code}")
    try:
        # Prevent crashing if Modrinth responds with a non-JSON error page
        error_body = response.json()
        print(f"Error: {error_body.get('error', 'Unknown Error')}")
        print(
            f"Description: {error_body.get('description', 'No description provided by API')}"
        )
    except ValueError:
        print("Raw response body (Non-JSON):")
        print(response.text[:500])  # Print first 500 chars to avoid flooding logs

    return response.status_code


def main() -> int:
    project_id = os.environ.get("MODRINTH_PROJECT_ID")
    repo = os.environ.get(
        "GITHUB_REPOSITORY"
    )  # e.g., "username/repo" provided by GitHub Actions
    branch = os.environ.get(
        "GITHUB_REF_NAME", "main"
    )  # Defaults to current workflow branch (e.g., "main")

    if not project_id:
        print("❌ Error: MODRINTH_PROJECT_ID is not set.")
        return 1
    if not repo:
        print(
            "❌ Error: GITHUB_REPOSITORY is not set. Are you running this outside of GitHub Actions?"
        )
        return 1

    readme_path = "README.md"
    if not os.path.exists(readme_path):
        print(f"❌ Error: {readme_path} not found.")
        return 1

    with open(readme_path, "r", encoding="utf-8") as f:
        content = f.read()

    # Apply path transformations
    processed_content = rewrite_relative_paths(content, repo, branch)

    return update_modrinth_desc(project_id, processed_content)


if __name__ == "__main__":
    sys.exit(main())
