export class Menu {
  constructor(
    public name: string = '',
    public link: string = '',
    public title: string = '',
    public children: Menu[] = [],
    public thumbnail: string = '',
    public specialClassCss: boolean = false,
    public isActive: boolean = false
  ) {
  }
}


export class MenuAdmin {
  constructor(
    public name: string = '',
    public icon: string = '',
    public path: string = '',
    public children: ChildMenu[] = [],
    public active: boolean = false,
    public key: string = '',
    public expand: boolean = false
  ) {
  }
}

export class ChildMenu {
  constructor(
    public name: string = '',
    public path: string = '',
    public active: boolean = false,
  ) {
  }
}
