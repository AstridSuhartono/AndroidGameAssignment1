package com.mygdx.game;

public class ConstantVal {
    float playerGravity = 4f;
    float enemyProjectileGravity = 150f;

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
    float aWidth = 200;
    float aHeight = 200;
    float aMoveSpeed = 120;
}

