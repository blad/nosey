NoseyExplorer - Realm.io Android Data Explorer
======

NoseyExplorer is a simple explorer that lets you see the data that Realm.io has saved for your models.

Simply register your models and your ready to start browsing your data from within your app.

## Usage

### Including NoseyExplorer:

```
cd project-root
git submodule add nosey git@github.com:blad/nosey.git
```

`Settings.gradle`:
```Groovy
include ':my-project', ':noseyexplorer'
project(':noseyexplorer').projectDir = new File(settingsDir, 'nosey/noseyexplorer')
```

Main project's `build.gradle`:
```Groovy
dependencies {
  compile project(':noseyexplorer')
}
```

### Using NoseyExplorer to Browse Data:

```Java
Nosey.getInstance(context)
  // Register Your Models with NoseyExplorer:
  .register(MyModelNumberOne.class)
  .register(MyModelNumberTwo.class)
  .register(MyModelNumberThree.class)
  .register(MyAwesomeModel.class)
  // Call start to open the Activity to Browse the Models
  .start();
```

## License

>   Copyright 2015 Bladymir Tellez
>
> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at
>
> http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.
