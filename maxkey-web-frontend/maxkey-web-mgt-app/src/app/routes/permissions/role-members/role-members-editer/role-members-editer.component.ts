

import { ChangeDetectionStrategy, ViewContainerRef, Input, ChangeDetectorRef, Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { I18NService } from '@core';
import { _HttpClient, ALAIN_I18N_TOKEN, SettingsService } from '@delon/theme';
import { format, addDays } from 'date-fns';
import { NzSafeAny } from 'ng-zorro-antd/core/types';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzModalRef, NzModalService } from 'ng-zorro-antd/modal';
import { NzTableQueryParams } from 'ng-zorro-antd/table';

import { Roles } from '../../../../entity/Roles';
import { RoleMembersService } from '../../../../service/role-members.service';
@Component({
  selector: 'app-role-members-editer',
  templateUrl: './role-members-editer.component.html',
  styleUrls: ['./role-members-editer.component.less']
})
export class RoleMembersEditerComponent implements OnInit {
  @Input() roleId?: String;
  @Input() isEdit?: boolean;

  query: {
    params: {
      name: String;
      displayName: String;
      username: String;
      roleId: String;
      startDate: String;
      endDate: String;
      startDatePicker: Date;
      endDatePicker: Date;
      pageSize: number;
      pageNumber: number;
      pageSizeOptions: number[];
    };
    results: {
      records: number;
      rows: NzSafeAny[];
    };
    expandForm: Boolean;
    submitLoading: boolean;
    tableLoading: boolean;
    tableCheckedId: Set<String>;
    indeterminate: boolean;
    checked: boolean;
  } = {
    params: {
      name: '',
      displayName: '',
      username: '',
      roleId: '',
      startDate: '',
      endDate: '',
      startDatePicker: addDays(new Date(), -30),
      endDatePicker: new Date(),
      pageSize: 5,
      pageNumber: 1,
      pageSizeOptions: [5, 15, 50]
    },
    results: {
      records: 0,
      rows: []
    },
    expandForm: false,
    submitLoading: false,
    tableLoading: false,
    tableCheckedId: new Set<String>(),
    indeterminate: false,
    checked: false
  };

  constructor(
    private modalRef: NzModalRef,
    private roleMembersService: RoleMembersService,
    private viewContainerRef: ViewContainerRef,
    private fb: FormBuilder,
    private msg: NzMessageService,
    @Inject(ALAIN_I18N_TOKEN) private i18n: I18NService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    if (this.roleId) {
      this.query.params.roleId = this.roleId;
      this.fetch();
    }
  }

  onQueryParamsChange(tableQueryParams: NzTableQueryParams): void {
    this.query.params.pageNumber = tableQueryParams.pageIndex;
    this.query.params.pageSize = tableQueryParams.pageSize;
    this.fetch();
  }

  onSearch(): void {
    this.fetch();
  }

  onReset(): void {}

  fetch(): void {
    this.query.submitLoading = true;
    this.query.tableLoading = true;
    this.query.indeterminate = false;
    this.query.checked = false;
    this.query.tableCheckedId.clear();
    if (this.query.expandForm) {
      this.query.params.endDate = format(this.query.params.endDatePicker, 'yyyy-MM-dd HH:mm:ss');
      this.query.params.startDate = format(this.query.params.startDatePicker, 'yyyy-MM-dd HH:mm:ss');
    } else {
      this.query.params.endDate = '';
      this.query.params.startDate = '';
    }
    this.roleMembersService.memberOut(this.query.params).subscribe(res => {
      this.query.results = res.data;
      this.query.submitLoading = false;
      this.query.tableLoading = false;
      this.cdr.detectChanges();
    });
  }

  updateTableCheckedSet(id: String, checked: boolean): void {
    if (checked) {
      this.query.tableCheckedId.add(id);
    } else {
      this.query.tableCheckedId.delete(id);
    }
  }

  refreshTableCheckedStatus(): void {
    const listOfEnabledData = this.query.results.rows.filter(({ disabled }) => !disabled);
    this.query.checked = listOfEnabledData.every(({ id }) => this.query.tableCheckedId.has(id));
    this.query.indeterminate = listOfEnabledData.some(({ id }) => this.query.tableCheckedId.has(id)) && !this.query.checked;
  }

  onTableItemChecked(id: String, checked: boolean): void {
    //this.onTableAllChecked(false);
    this.updateTableCheckedSet(id, checked);
    this.refreshTableCheckedStatus();
  }

  onTableAllChecked(checked: boolean): void {
    this.query.results.rows.filter(({ disabled }) => !disabled).forEach(({ id }) => this.updateTableCheckedSet(id, checked));
    this.refreshTableCheckedStatus();
  }

  onSubmit(e: MouseEvent): void {
    e.preventDefault();
    const listOfEnabledData = this.query.results.rows.filter(({ disabled }) => !disabled);
    let selectedData = listOfEnabledData.filter(({ id, name }) => {
      return this.query.tableCheckedId.has(id);
    });
    let memberIds = '';
    let memberNames = '';
    for (let i = 0; i < selectedData.length; i++) {
      memberIds = `${memberIds},${selectedData[i].id}`;
      memberNames = `${memberNames},${selectedData[i].username}`;
    }
    this.roleMembersService.add({ type: 'USER', roleId: this.roleId, memberId: memberIds, memberName: memberNames }).subscribe(res => {
      this.query.results = res.data;
      this.query.submitLoading = false;
      this.query.tableLoading = false;
      if (res.code == 0) {
        this.msg.success(this.i18n.fanyi('mxk.alert.operate.success'));
        this.fetch();
      } else {
        this.msg.error(this.i18n.fanyi('mxk.alert.operate.error'));
      }
      this.cdr.detectChanges();
    });
  }

  onClose(e: MouseEvent): void {
    e.preventDefault();
  }
}
