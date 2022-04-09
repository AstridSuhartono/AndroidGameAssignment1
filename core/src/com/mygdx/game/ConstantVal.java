package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;

public class ConstantVal {
    float playerGravity = 4f;
    float enemyProjectileGravity = 150f;
    float bossGravity = MathUtils.random(135f, 150f);

    //player character
    float characterX = 40;
    float characterY = 40;
    float width = 72;
    float height = 72;
    float moveSpeed = 100;
    double shootDelay = 3e+9;

    //projectile
    float pWidth = 28;
    float pHeight = 28;
    float projectileSpeed = 300;

    //ground enemy
    float gCharacterX = 800;
    float gCharacterY = 30;
    float gWidth = 96;
    float gHeight = 156;
    float gMoveSpeed = 180;

    //air enemy
    float aCharacterX = 0;
    float aCharacterY = 264;
    float aWidth = 180;
    float aHeight = 180;
    float aMoveSpeed = 120;

    //boss enemy
    float bCharacterX = 500;
    float bCharacterY = 30;
    float bWidth = 250;
    float bHeight = 250;
    float bossShootDelay = 6e+9f;
    float bProjSpeed = -200f;
    Integer health = 4;
}

