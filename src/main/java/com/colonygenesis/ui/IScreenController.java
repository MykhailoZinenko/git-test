package com.colonygenesis.ui;

import javafx.scene.Parent;

public interface IScreenController {
    Parent getRoot();
    void initialize();
    void onShow();
    void onHide();
    void update();
}