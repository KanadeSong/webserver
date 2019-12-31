package com.seater.user.entity;

public enum ProjectStatus {
        Unknown("未知"),
        Start("开工"),
        Stop("停工");

        private String value;
    ProjectStatus(String value)
        {
            this.value = value;
        }

        @Override
        public String toString()
        {
            return this.value;
        }
}
