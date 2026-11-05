# BBS Blur Clips (addon for BBS FS, Fabric 1.20.1)

Adds a **Blur** camera clip to BBS FS with three modes:

| Mode | What it does |
|------|--------------|
| Circle | Middle of the screen stays sharp, everything around it is blurred |
| Full blur | The whole screen is blurred |
| Actor focus | The chosen actor (replay index) stays sharp, everything else is blurred |

Clip settings: strength, circle radius, edge softness, actor index, actor margin.
The clip's envelope (fade in / fade out) also fades the blur in and out.

Built against **BBS FS 2.5.2-1.20.1** (`libs/bbs-2_5_2-1_20_1.jar`).

## Build on GitHub (no local setup)

1. Create an empty repo on github.com (no README).
2. In this folder:
   ```
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/YOURNAME/YOURREPO.git
   git push -u origin main
   ```
   When asked for a password, use a Personal Access Token (GitHub > Settings > Developer settings > Personal access tokens).
3. Open the repo's **Actions** tab. The `build` workflow runs on every push.
4. Open the finished run and download the **bbs-blur-addon** artifact (a zip containing the jar).
   Use `bbs-blur-addon-1.0.0.jar`, not the `-sources` one.

Later changes: `git add .`, `git commit -m "msg"`, `git push`.

## Install

Put `bbs-blur-addon-1.0.0.jar` in `mods/` next to BBS FS 2.5.2, Fabric API, and Fabric Loader. Then in the film editor: Camera clips > Add clip > **Blur**.

## Local build (optional)

Needs JDK 17 and Gradle 8.8+: `gradle build` -> `build/libs/`.
